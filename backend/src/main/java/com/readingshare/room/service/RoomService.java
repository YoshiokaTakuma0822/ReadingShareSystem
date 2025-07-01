package com.readingshare.room.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.chat.domain.repository.IChatMessageRepository;
import com.readingshare.common.exception.ApplicationException;
import com.readingshare.common.exception.DatabaseAccessException;
import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.model.RoomMember;
import com.readingshare.room.domain.repository.IRoomRepository;
import com.readingshare.room.domain.repository.IRoomMemberRepository;
import com.readingshare.room.domain.service.RoomDomainService;
import com.readingshare.room.dto.CreateRoomRequest;
import com.readingshare.room.dto.UpdateRoomRequest;
import com.readingshare.survey.domain.repository.ISurveyAnswerRepository;
import com.readingshare.survey.domain.repository.ISurveyRepository;

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

    @Autowired
    private IChatMessageRepository chatMessageRepository;
    @Autowired
    private ISurveyRepository surveyRepository;
    @Autowired
    private ISurveyAnswerRepository surveyAnswerRepository;

    public RoomService(IRoomRepository roomRepository, RoomDomainService roomDomainService,
            IRoomMemberRepository roomMemberRepository) {
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

    public Room createRoomWithPassword(String roomName, String bookTitle, UUID hostUserId, String roomPassword,
            int totalPages) {
        Room newRoom = new Room(roomName, bookTitle, hostUserId, totalPages);
        return roomDomainService.createRoom(newRoom, roomPassword);
    }

    /**
     * 部屋作成(CreateRoomRequest) - ジャンルと開始時刻を含む
     */
    public Room createRoom(CreateRoomRequest request) {
        // デフォルトページ数
        int pages = request.totalPages() != null ? request.totalPages() : 300;
        Room newRoom = new Room(request.roomName(), request.bookTitle(), request.hostUserId(), pages);
        newRoom.setGenre(request.genre());
        if (request.startTime() != null) {
            newRoom.setStartTime(request.startTime().atZone(ZoneId.systemDefault()).toInstant());
        }
        if (request.endTime() != null) {
            newRoom.setEndTime(request.endTime().atZone(ZoneId.systemDefault()).toInstant());
        }
        String password = request.password();
        return roomDomainService.createRoom(newRoom, password);
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
        List<Room> result;
        if (keyword == null || keyword.trim().isEmpty()) {
            result = roomRepository.findAll();
        } else {
            result = roomRepository.findByKeyword(keyword);
        }
        // パスワード有無をセット
        for (Room room : result) {
            room.setHasPassword(room.getPasswordHash() != null && !room.getPasswordHash().isEmpty());
        }
        return result;
    }

    /**
     * 複数条件で部屋を検索する。
     */
    @Transactional(readOnly = true)
    public List<Room> searchRooms(
            String keyword,
            String genre,
            LocalDateTime startFrom,
            LocalDateTime startTo,
            LocalDateTime endFrom,
            LocalDateTime endTo,
            Integer pagesMin,
            Integer pagesMax) {
        // パラメータをInstantに変換
        Instant startFromI = (startFrom != null) ? startFrom.atZone(ZoneId.systemDefault()).toInstant() : null;
        Instant startToI = (startTo != null) ? startTo.atZone(ZoneId.systemDefault()).toInstant() : null;
        Instant endFromI = (endFrom != null) ? endFrom.atZone(ZoneId.systemDefault()).toInstant() : null;
        Instant endToI = (endTo != null) ? endTo.atZone(ZoneId.systemDefault()).toInstant() : null;
        // DBレベルで条件検索
        List<Room> rooms = roomRepository.findByConditions(
                keyword, genre, startFromI, startToI, endFromI, endToI, pagesMin, pagesMax);
        // パスワード有無をセット
        for (Room room : rooms) {
            room.setHasPassword(room.getPasswordHash() != null && !room.getPasswordHash().isEmpty());
        }
        return rooms;
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
        Optional<Room> opt = roomRepository.findById(roomId);
        opt.ifPresent(room -> room.setHasPassword(room.getPasswordHash() != null && !room.getPasswordHash().isEmpty()));
        return opt;
    }

    /**
     * 部屋一覧を取得する。
     *
     * @param limit 取得する部屋数の上限
     * @return 部屋のリスト（作成日時の新しい順）
     */
    @Transactional(readOnly = true)
    public List<Room> getRooms(int limit) {
        if (limit <= 0 || limit > 100) {
            limit = 10; // デフォルト値
        }
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Room> result = roomRepository.findAll(pageable).getContent();
        for (Room room : result) {
            room.setHasPassword(room.getPasswordHash() != null && !room.getPasswordHash().isEmpty());
        }
        return result;
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
     *
     * @param roomId 部屋ID（UUID文字列）
     */
    @Transactional
    public void deleteRoom(String roomId) {
        UUID uuid = UUID.fromString(roomId);
        Room room = roomRepository.findById(uuid)
                .orElseThrow(() -> new ApplicationException("部屋が見つかりません"));

        // 1. メンバー削除
        List<RoomMember> members = roomMemberRepository.findByRoom(room);
        for (RoomMember m : members) {
            roomMemberRepository.delete(m);
        }

        // 2. チャット削除
        List<com.readingshare.chat.domain.model.ChatMessage> messages = chatMessageRepository.findByRoom(room);
        for (com.readingshare.chat.domain.model.ChatMessage msg : messages) {
            chatMessageRepository.delete(msg);
        }

        // 3. アンケート・回答削除
        surveyRepository.deleteByRoomId(uuid);

        // 4. UserProgress削除（RoomReadingStateService等で部屋単位削除APIを呼ぶ必要あり）
        // 例: roomReadingStateService.deleteByRoomId(roomId);

        // 5. 部屋本体削除
        roomRepository.deleteById(uuid);
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
        if (request.genre() != null) {
            room.setGenre(request.genre());
        }
        if (request.startTime() != null) {
            room.setStartTime(request.startTime().atZone(ZoneId.systemDefault()).toInstant());
        }
        if (request.endTime() != null) {
            room.setEndTime(request.endTime().atZone(ZoneId.systemDefault()).toInstant());
        }
        return roomRepository.save(room);
    }

    /**
     * 指定ユーザーが参加したことのある部屋の履歴（最新N件）を返す
     */
    @Transactional(readOnly = true)
    public List<RoomMember> getRoomHistory(UUID userId, int limit) {
        return roomMemberRepository.findByUserIdOrderByJoinedAtDesc(userId, PageRequest.of(0, limit));
    }

    /**
     * 指定ユーザーの全参加履歴を削除する（履歴リセット）
     */
    @Transactional
    public void deleteRoomHistory(UUID userId) {
        // ユーザーの参加履歴を全件取得して削除
        List<RoomMember> members = roomMemberRepository.findByUserIdOrderByJoinedAtDesc(userId,
                PageRequest.of(0, Integer.MAX_VALUE));
        for (RoomMember m : members) {
            roomMemberRepository.delete(m);
        }
    }
}
