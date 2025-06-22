package com.readingshare.room.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.auth.domain.repository.IUserRepository;
import com.readingshare.common.exception.ApplicationException;
import com.readingshare.common.exception.DatabaseAccessException;
import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.model.RoomMember;
import com.readingshare.room.domain.repository.IRoomRepository;
import com.readingshare.room.domain.service.RoomDomainService;
import com.readingshare.room.dto.RoomResponse;
import com.readingshare.room.dto.UpdateRoomRequest;

/**
 * 部屋に関する統合アプリケーションサービス。
 * 部屋の作成、検索、参加などの機能を提供する。
 * 担当: 芳岡
 */
@Service
public class RoomService {

    private final IRoomRepository roomRepository;
    private final RoomDomainService roomDomainService;
    private final IUserRepository userRepository;

    public RoomService(IRoomRepository roomRepository, RoomDomainService roomDomainService, IUserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.roomDomainService = roomDomainService;
        this.userRepository = userRepository;
    }

    public RoomResponse toRoomResponse(Room room) {
        String username = userRepository.findById(room.getHostUserId())
            .map(u -> u.getUsername()).orElse("匿名");
        return new RoomResponse(
            room.getId(),
            room.getRoomName(),
            room.getBookTitle(),
            room.getHostUserId(),
            username,
            room.getTotalPages(), // 修正
            room.getGenre(),
            room.getStartTime(),
            room.getEndTime(),
            room.getPageTurnSpeed() // 修正
        );
    }

    // =============== 部屋作成関連 ===============

    /**
     * 新しい部屋を作成する。
     *
     * @param roomName   部屋名
     * @param bookTitle  読んでいる本のタイトル
     * @param hostUserId 部屋を作成するユーザーのID（ホスト）
     * @return 作成された部屋のエンティティ
     * @throws ApplicationException 部屋作成に失敗した場合
     */
    public Room createRoom(String roomName, String bookTitle, UUID hostUserId, Integer totalPages, String genre, Instant startTime, Instant endTime, Integer pageTurnSpeed) {
        Room newRoom = new Room(roomName, bookTitle, hostUserId, totalPages, genre, startTime, endTime, pageTurnSpeed);
        // パスワードはオプションなので、ここではnullを渡す
        return roomDomainService.createRoom(newRoom, null);
    }

    /**
     * パスワード付きの新しい部屋を作成する。
     *
     * @param roomName     部屋名
     * @param bookTitle    読んでいる本のタイトル
     * @param hostUserId   部屋を作成するユーザーのID（ホスト）
     * @param roomPassword 部屋のパスワード
     * @return 作成された部屋のエンティティ
     * @throws ApplicationException 部屋作成に失敗した場合
     */
    public Room createRoomWithPassword(String roomName, String bookTitle, UUID hostUserId, String roomPassword, Integer totalPages, String genre, Instant startTime, Instant endTime, Integer pageTurnSpeed) {
        Room newRoom = new Room(roomName, bookTitle, hostUserId, totalPages, genre, startTime, endTime, pageTurnSpeed);
        return roomDomainService.createRoom(newRoom, roomPassword);
    }

    // =============== 部屋検索・取得関連 ===============

    /**
     * キーワードに基づいて部屋を検索する。
     *
     * @param keyword 検索キーワード（nullまたは空文字列の場合は全件検索）
     * @return 検索結果の部屋リスト
     * @throws DatabaseAccessException データベースアクセスエラー時
     */
    @Transactional(readOnly = true)
    public List<RoomResponse> searchRooms(String keyword) {
        List<Room> rooms = (keyword == null || keyword.trim().isEmpty())
            ? roomRepository.findAll()
            : roomRepository.findByKeyword(keyword);
        return rooms.stream().map(this::toRoomResponse).toList();
    }

    /**
     * ジャンルで部屋を検索する
     */
    @Transactional(readOnly = true)
    public List<RoomResponse> searchRoomsByGenre(String genre) {
        return roomRepository.findByGenre(genre).stream().map(this::toRoomResponse).toList();
    }

    /**
     * 部屋IDに基づいて部屋を検索する。
     *
     * @param roomId 部屋ID
     * @return 部屋が見つかった場合はOptionalにRoom、見つからない場合はOptional.empty()
     * @throws DatabaseAccessException データベースアクセスエラー時
     */
    @Transactional(readOnly = true)
    public Optional<RoomResponse> getRoomById(UUID roomId) {
        return roomRepository.findById(roomId).map(this::toRoomResponse);
    }

    /**
     * 部屋一覧を取得する。
     *
     * @param limit 取得する部屋数の上限
     * @return 部屋のリスト（作成日時の新しい順）
     */
    @Transactional(readOnly = true)
    public List<RoomResponse> getRooms(int limit) {
        // limitの妥当性チェック
        if (limit <= 0 || limit > 100) {
            limit = 10; // デフォルト値
        }

        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return roomRepository.findAll(pageable).getContent().stream().map(this::toRoomResponse).toList();
    }

    // =============== 部屋参加関連 ===============

    /**
     * ユーザーを部屋に参加させる。
     *
     * @param roomId       参加する部屋のID
     * @param userId       参加するユーザーのID
     * @param roomPassword 部屋のパスワード（パスワード保護された部屋の場合）
     * @return 作成されたRoomMemberエンティティ
     * @throws ApplicationException 部屋が見つからない、パスワードが間違っている、既に部屋に参加している場合など
     */
    public RoomMember joinRoom(UUID roomId, UUID userId, String roomPassword) {
        return roomDomainService.addRoomMember(roomId, userId, roomPassword);
    }

    /**
     * 部屋の情報を更新する。
     *
     * @param roomId  更新する部屋のID
     * @param request 更新内容を含むリクエスト
     * @return 更新された部屋のエンティティ
     * @throws ApplicationException 部屋が見つからない場合
     */
    @Transactional
    public Room updateRoom(String roomId, UpdateRoomRequest request) {
        UUID id = UUID.fromString(roomId);
        Room room = roomRepository.findById(id).orElseThrow(() -> new ApplicationException("部屋が見つかりません"));
        if (request.getTotalPages() != null) {
            room.setTotalPages(request.getTotalPages());
        }
        // 必要なら他の項目もここで更新
        return roomRepository.save(room);
    }

    /**
     * 部屋を削除する。
     *
     * @param roomId 削除する部屋のID
     * @throws ApplicationException 部屋が見つからない場合
     */
    @Transactional
    public void deleteRoom(String roomId) {
        UUID id = UUID.fromString(roomId);
        roomRepository.deleteById(id);
    }
}
