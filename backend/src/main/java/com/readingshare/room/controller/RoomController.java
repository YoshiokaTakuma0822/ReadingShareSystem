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

import com.readingshare.auth.domain.repository.IUserRepository;
import com.readingshare.auth.domain.model.User;
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
    public ResponseEntity<List<Room>> getRooms(@RequestParam(value = "limit", defaultValue = "10") int limit) {
        List<Room> rooms = roomService.getRooms(limit);
        return ResponseEntity.ok(rooms);
    }

    /**
     * 部屋参加エンドポイント
     * POST /api/rooms/join
     */
    @PostMapping("/join")
    public ResponseEntity<RoomMember> joinRoom(@RequestBody JoinRoomRequest request) {
        RoomMember roomMember = roomService.joinRoom(request.roomId(), request.userId(), request.roomPassword());
        return ResponseEntity.ok(roomMember);
    }

    /**
     * 部屋検索エンドポイント
     * GET /api/rooms/search?keyword=xxx
     */
    @GetMapping("/search")
    public ResponseEntity<List<Room>> searchRooms(@RequestParam String keyword) {
        List<Room> rooms = roomService.searchRooms(keyword);
        return ResponseEntity.ok(rooms);
    }

    /**
     * 部屋詳細取得エンドポイント
     * GET /api/rooms/{roomId}
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<Room> getRoomById(@PathVariable("roomId") String roomId) {
        return roomService.getRoomById(UUID.fromString(roomId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
}
