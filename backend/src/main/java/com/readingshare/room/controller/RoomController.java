package com.readingshare.room.controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.readingshare.auth.domain.model.User;
import com.readingshare.auth.domain.repository.IUserRepository;
import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.model.RoomMember;
import com.readingshare.room.dto.CreateRoomRequest;
import com.readingshare.room.dto.JoinRoomRequest;
import com.readingshare.room.dto.UpdateRoomRequest;
import com.readingshare.room.service.RoomService;

/**
 * REST API コントローラー - 部屋作成 / 参加 / 検索
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final IUserRepository userRepository;

    public RoomController(RoomService roomService, IUserRepository userRepository,
            SimpMessagingTemplate messagingTemplate) {
        this.roomService = roomService;
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
    }

    /**
     * 部屋作成エンドポイント
     * POST /api/rooms
     */
    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody CreateRoomRequest request) {
        Room createdRoom = roomService.createRoom(request);
        // Broadcast new room
        messagingTemplate.convertAndSend("/topic/rooms", createdRoom);
        return ResponseEntity.ok(createdRoom);
    }

    /**
     * 部屋一覧取得エンドポイント
     * GET /api/rooms?limit=10
     */
    @GetMapping
    public ResponseEntity<List<Room>> getRooms(@RequestParam(value = "limit", defaultValue = "10") int limit) {
        List<Room> rooms = roomService.getRooms(limit);
        return ResponseEntity.ok(rooms);
    }

    /**
     * 部屋参加エンドポイント
     * POST /api/rooms/join
     */
    @PostMapping("/join")
    public ResponseEntity<?> joinRoom(@RequestBody JoinRoomRequest request) {
        try {
            RoomMember roomMember = roomService.joinRoom(request.roomId(), request.userId(), request.roomPassword());
            // Broadcast join event for user history
            Room room = roomMember.getRoom();
            RoomHistoryDto historyDto = new RoomHistoryDto(room.getId(), room, false, roomMember.getJoinedAt());
            messagingTemplate.convertAndSend("/topic/history/" + request.userId(), historyDto);
            return ResponseEntity.ok(roomMember);
        } catch (com.readingshare.common.exception.DomainException ex) {
            // パスワード不一致や既にメンバー等の業務例外は400で返す
            return ResponseEntity.badRequest().body(java.util.Map.of("message", ex.getMessage()));
        }
    }

    /**
     * 部屋検索エンドポイント
     * GET /api/rooms/search?keyword=xxx
     */
    @GetMapping("/search")
    public ResponseEntity<List<Room>> searchRooms(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTo,
            @RequestParam(required = false) Integer pagesMin,
            @RequestParam(required = false) Integer pagesMax) {
        List<Room> rooms = roomService.searchRooms(keyword, genre, startFrom, startTo, endFrom, endTo, pagesMin,
                pagesMax);
        return ResponseEntity.ok(rooms);
    }

    /**
     * 部屋詳細取得エンドポイント
     * GET /api/rooms/{roomId}
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<Room> getRoomById(@PathVariable("roomId") String roomId) {
        System.out.println("[DEBUG] getRoomById called. roomId=" + roomId);
        return roomService.getRoomById(UUID.fromString(roomId))
                .map(room -> {
                    System.out.println("[DEBUG] Room found: " + room.getId() + ", hostUserId=" + room.getHostUserId());
                    return ResponseEntity.ok(room);
                })
                .orElseGet(() -> {
                    System.out.println("[DEBUG] Room not found: " + roomId);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * 部屋削除エンドポイント
     * DELETE /api/rooms/{roomId}
     */
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable("roomId") String roomId) {
        roomService.deleteRoom(roomId);
        // Broadcast room deletion
        messagingTemplate.convertAndSend("/topic/rooms", Map.of("type", "delete", "roomId", roomId));
        return ResponseEntity.noContent().build();
    }

    /**
     * 部屋情報更新エンドポイント
     * PUT /api/rooms/{roomId}
     */
    @PutMapping("/{roomId}")
    public ResponseEntity<Room> updateRoom(@PathVariable("roomId") String roomId,
            @RequestBody UpdateRoomRequest request) {
        Room updatedRoom = roomService.updateRoom(UUID.fromString(roomId), request);
        return ResponseEntity.ok(updatedRoom);
    }

    /**
     * 部屋メンバー一覧取得エンドポイント
     * GET /api/rooms/{roomId}/members
     */
    @GetMapping("/{roomId}/members")
    public ResponseEntity<List<MemberInfoDto>> getRoomMembers(@PathVariable("roomId") String roomId) {
        UUID uuid = UUID.fromString(roomId);
        // 部屋情報を取得し、存在しなければ404
        Optional<Room> optRoom = roomService.getRoomById(uuid);
        if (optRoom.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Room room = optRoom.get();
        // RoomMember から参加メンバーを取得
        List<RoomMember> members = roomService.getRoomMembers(uuid);
        List<MemberInfoDto> result = new ArrayList<>();
        // ホストを先頭に追加 (作成時刻を参加時刻として扱う)
        String hostName = userRepository.findById(room.getHostUserId()).map(User::getUsername).orElse("");
        result.add(new MemberInfoDto(room.getHostUserId(), hostName, room.getCreatedAt()));
        // その他参加メンバーを追加
        for (RoomMember m : members) {
            if (!m.getUserId().equals(room.getHostUserId())) {
                String name = userRepository.findById(m.getUserId()).map(User::getUsername).orElse("");
                result.add(new MemberInfoDto(m.getUserId(), name, m.getJoinedAt()));
            }
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 指定ユーザーが参加したことのある部屋の履歴（最新10件）
     * GET /api/rooms/history?userId=xxx&limit=10
     */
    @GetMapping("/history")
    public ResponseEntity<List<RoomHistoryDto>> getRoomHistory(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "10") int limit) {

        // ユーザーの履歴リセット時刻を取得
        Optional<User> optUser = userRepository.findById(userId);
        Instant historyResetAt = optUser.flatMap(u -> Optional.ofNullable(u.getHistoryResetAt())).orElse(Instant.EPOCH);

        List<RoomMember> members = roomService.getRoomHistory(userId, limit + 10); // 余分に取得
        // フィルタ: ホストの自動参加 (createdAt == joinedAt) を除外 ＆ 履歴リセット時刻より後のみ
        List<RoomHistoryDto> result = members.stream()
                .filter(member -> {
                    Optional<Room> optRoom = roomService.getRoomById(member.getRoom().getId());
                    if (optRoom.isEmpty())
                        return true; // 削除済みは表示
                    Room room = optRoom.get();
                    boolean isHost = room.getHostUserId().equals(member.getUserId());
                    boolean isAuto = isHost && member.getJoinedAt().equals(room.getCreatedAt());
                    boolean afterReset = member.getJoinedAt().isAfter(historyResetAt);
                    return !isAuto && afterReset;
                })
                .map(member -> {
                    Room room = roomService.getRoomById(member.getRoom().getId()).orElse(null);
                    boolean deleted = (room == null);
                    return new RoomHistoryDto(member.getRoom().getId(), room, deleted, member.getJoinedAt());
                })
                .limit(limit)
                .toList();
        return ResponseEntity.ok(result);
    }

    /**
     * ユーザーの部屋参加履歴を削除（リセット）
     * DELETE /api/rooms/history?userId=xxx
     */
    @DeleteMapping("/history")
    public ResponseEntity<Void> deleteRoomHistory(@RequestParam("userId") UUID userId) {
        // Delete user's room join history
        roomService.deleteRoomHistory(userId);
        // 履歴リセット時刻を記録
        userRepository.findById(userId).ifPresent(user -> {
            user.setHistoryResetAt(Instant.now());
            userRepository.save(user);
        });
        // Broadcast reset event for real-time history clearing
        messagingTemplate.convertAndSend("/topic/history/" + userId, Map.of("type", "reset"));
        return ResponseEntity.noContent().build();
    }

    // DTOクラス
    public static class MemberInfoDto {
        public UUID userId;
        public String username;
        public java.time.Instant joinedAt;

        public MemberInfoDto(UUID userId, String username, java.time.Instant joinedAt) {
            this.userId = userId;
            this.username = username;
            this.joinedAt = joinedAt;
        }
    }

    // 履歴DTO
    public static class RoomHistoryDto {
        public UUID roomId;
        public Room room; // nullなら削除済み
        public boolean deleted;
        public java.time.Instant joinedAt;

        public RoomHistoryDto(UUID roomId, Room room, boolean deleted, java.time.Instant joinedAt) {
            this.roomId = roomId;
            this.room = room;
            this.deleted = deleted;
            this.joinedAt = joinedAt;
        }
    }
}
