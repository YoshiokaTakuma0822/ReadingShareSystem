package com.readingshare.room.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.common.exception.ApplicationException;
import com.readingshare.common.exception.DatabaseAccessException;
import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.model.RoomMember;
import com.readingshare.room.domain.repository.IRoomRepository;
import com.readingshare.room.domain.repository.IRoomMemberRepository;
import com.readingshare.room.domain.service.RoomDomainService;
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
    private final IRoomMemberRepository roomMemberRepository;

    public RoomService(IRoomRepository roomRepository, RoomDomainService roomDomainService, IRoomMemberRepository roomMemberRepository) {
        this.roomRepository = roomRepository;
        this.roomDomainService = roomDomainService;
        this.roomMemberRepository = roomMemberRepository;
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
    public Room createRoom(String roomName, String bookTitle, UUID hostUserId) {
        Room newRoom = new Room(roomName, bookTitle, hostUserId);
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
    public Room createRoomWithPassword(String roomName, String bookTitle, UUID hostUserId, String roomPassword) {
        Room newRoom = new Room(roomName, bookTitle, hostUserId);
        return roomDomainService.createRoom(newRoom, roomPassword);
    }

    public Room createRoom(String roomName, String bookTitle, UUID hostUserId, int totalPages) {
        Room newRoom = new Room(roomName, bookTitle, hostUserId, totalPages);
        return roomDomainService.createRoom(newRoom, null);
    }

    public Room createRoomWithPassword(String roomName, String bookTitle, UUID hostUserId, String roomPassword, int totalPages) {
        Room newRoom = new Room(roomName, bookTitle, hostUserId, totalPages);
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
    public List<Room> searchRooms(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return roomRepository.findAll();
        } else {
            return roomRepository.findByKeyword(keyword);
        }
    }

    /**
     * 部屋IDに基づいて部屋を検索する。
     *
     * @param roomId 部屋ID
     * @return 部屋が見つかった場合はOptionalにRoom、見つからない場合はOptional.empty()
     * @throws DatabaseAccessException データベースアクセスエラー時
     */
    @Transactional(readOnly = true)
    public Optional<Room> getRoomById(UUID roomId) {
        return roomRepository.findById(roomId);
    }

    /**
     * 部屋一覧を取得する。
     *
     * @param limit 取得する部屋数の上限
     * @return 部屋のリスト（作成日時の新しい順）
     */
    @Transactional(readOnly = true)
    public List<Room> getRooms(int limit) {
        // limitの妥当性チェック
        if (limit <= 0 || limit > 100) {
            limit = 10; // デフォルト値
        }

        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return roomRepository.findAll(pageable).getContent();
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
     * 部屋を削除する。
     * @param roomId 部屋ID（UUID文字列）
     */
    public void deleteRoom(String roomId) {
        roomRepository.deleteById(UUID.fromString(roomId));
    }

    /**
     * 指定した部屋の全メンバーを返す
     */
    public List<RoomMember> getRoomMembers(UUID roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ApplicationException("部屋が見つかりません"));
        return roomMemberRepository.findByRoom(room);
    }

    /**
     * 部屋情報を更新する（totalPagesのみ）
     */
    @Transactional
    public Room updateRoom(UUID roomId, UpdateRoomRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ApplicationException("部屋が見つかりません"));
        if (request.totalPages() != null && request.totalPages() > 0) {
            room.setTotalPages(request.totalPages());
        }
        return roomRepository.save(room);
    }
}
