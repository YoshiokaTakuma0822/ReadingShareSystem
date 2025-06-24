package com.readingshare.room.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.readingshare.auth.domain.repository.IUserRepository;
import com.readingshare.auth.domain.model.User;
import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.model.RoomMember;
import com.readingshare.room.dto.CreateRoomRequest;
import com.readingshare.room.dto.JoinRoomRequest;
import com.readingshare.room.dto.UpdateRoomRequest;
import com.readingshare.room.service.RoomService;
import com.readingshare.auth.infrastructure.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST API コントローラー - 部屋作成 / 参加 / 検索
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;
    private final IUserRepository userRepository;

    public RoomController(RoomService roomService, IUserRepository userRepository) {
        this.roomService = roomService;
        this.userRepository = userRepository;
    }

    /**
     * 部屋作成エンドポイント
     * POST /api/rooms
     */
    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody CreateRoomRequest request) {
        Room createdRoom = request.password() != null
                ? roomService.createRoomWithPassword(request.roomName(), request.bookTitle(),
                        request.hostUserId(), request.password(), request.totalPages() != null ? request.totalPages() : 300)
                : roomService.createRoom(request.roomName(), request.bookTitle(), request.hostUserId(), request.totalPages() != null ? request.totalPages() : 300);
        return ResponseEntity.ok(createdRoom);
    }

    /**
     * 部屋一覧取得エンドポイント
     * GET /api/rooms?limit=10
     */
    @GetMapping
    public ResponseEntity<List<RoomResponse>> getRooms(@RequestParam(value = "limit", defaultValue = "10") int limit) {
        List<RoomResponse> rooms = roomService.getRooms(limit);
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
    public ResponseEntity<List<RoomResponse>> searchRooms(@RequestParam String keyword) {
        List<RoomResponse> rooms = roomService.searchRooms(keyword);
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
        return ResponseEntity.noContent().build();
    }

    /**
     * 部屋情報更新エンドポイント
     * PUT /api/rooms/{roomId}
     */
    @PutMapping("/{roomId}")
    public ResponseEntity<Room> updateRoom(@PathVariable("roomId") String roomId, @RequestBody UpdateRoomRequest request) {
        Room updatedRoom = roomService.updateRoom(UUID.fromString(roomId), request);
        return ResponseEntity.ok(updatedRoom);
    }

    /**
     * 部屋メンバー一覧取得エンドポイント
     * GET /api/rooms/{roomId}/members
     */
    @GetMapping("/{roomId}/members")
    public ResponseEntity<List<MemberInfoDto>> getRoomMembers(@PathVariable("roomId") String roomId) {
        List<RoomMember> members = roomService.getRoomMembers(UUID.fromString(roomId));
        // ユーザー名を取得してDTOに詰める
        List<MemberInfoDto> result = members.stream().map(member -> {
            String username = userRepository.findById(member.getUserId())
                .map(User::getUsername)
                .orElse("");
            return new MemberInfoDto(member.getUserId(), username, member.getJoinedAt());
        }).toList();
        return ResponseEntity.ok(result);
    }

    /**
     * 指定ユーザーが参加したことのある部屋の履歴（最新10件）
     * GET /api/rooms/history?userId=xxx&limit=10
     */
    @GetMapping("/history")
    public ResponseEntity<List<RoomHistoryDto>> getRoomHistory(
        @RequestParam("userId") UUID userId,
        @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<RoomMember> members = roomService.getRoomHistory(userId, limit);
        List<RoomHistoryDto> result = members.stream().map(member -> {
            Room room = roomService.getRoomById(member.getRoom().getId()).orElse(null);
            boolean deleted = (room == null);
            return new RoomHistoryDto(member.getRoom().getId(), room, deleted, member.getJoinedAt());
        }).toList();
        return ResponseEntity.ok(result);
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
