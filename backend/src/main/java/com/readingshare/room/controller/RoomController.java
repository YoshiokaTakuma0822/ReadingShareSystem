package com.readingshare.room.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.model.RoomMember;
import com.readingshare.room.dto.CreateRoomRequest;
import com.readingshare.room.dto.JoinRoomRequest;
import com.readingshare.room.dto.UpdateRoomRequest;
import com.readingshare.room.dto.RoomResponse;
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
    private static final Logger logger = LoggerFactory.getLogger(RoomController.class);

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    /**
     * 部屋作成エンドポイント
     * POST /api/rooms
     */
    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(@RequestBody CreateRoomRequest request) {
        try {
            RoomResponse createdRoom = request.password() != null
                    ? roomService.toRoomResponse(roomService.createRoomWithPassword(
                        request.roomName(),
                        request.bookTitle(),
                        request.hostUserId(),
                        request.password(),
                        request.maxPage(),
                        request.genre(),
                        request.startTime(),
                        request.endTime(),
                        request.pageSpeed()))
                    : roomService.toRoomResponse(roomService.createRoom(
                        request.roomName(),
                        request.bookTitle(),
                        request.hostUserId(),
                        request.maxPage(),
                        request.genre(),
                        request.startTime(),
                        request.endTime(),
                        request.pageSpeed()));
            return ResponseEntity.ok(createdRoom);
        } catch (Exception e) {
            logger.error("部屋作成APIで例外", e);
            return ResponseEntity.badRequest().body(null);
        }
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
    public ResponseEntity<RoomMember> joinRoom(@RequestBody JoinRoomRequest request) {
        RoomMember roomMember = roomService.joinRoom(request.roomId(), request.userId(), request.roomPassword());
        return ResponseEntity.ok(roomMember);
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
     * ジャンル検索エンドポイント
     * GET /api/rooms/genre?genre=xxx
     */
    @GetMapping("/genre")
    public ResponseEntity<List<RoomResponse>> searchRoomsByGenre(@RequestParam String genre) {
        List<RoomResponse> rooms = roomService.searchRoomsByGenre(genre);
        return ResponseEntity.ok(rooms);
    }

    /**
     * 部屋情報更新エンドポイント
     * PATCH /api/rooms/{roomId}
     */
    @PatchMapping("/{roomId}")
    public ResponseEntity<Room> updateRoom(@PathVariable("roomId") String roomId, @RequestBody UpdateRoomRequest request) {
        Room updated = roomService.updateRoom(roomId, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * 部屋削除エンドポイント
     * DELETE /api/rooms/{roomId}
     */
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable("roomId") String roomId) {
        // 認証ユーザーID取得
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            return ResponseEntity.status(403).build();
        }
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UUID userId = userPrincipal.getUserId();
        // 部屋情報取得
        Room room = roomService.getRoomById(UUID.fromString(roomId)).map(r -> {
            Room entity = new Room();
            entity.setId(r.getId());
            entity.setHostUserId(r.getHostUserId());
            return entity;
        }).orElse(null);
        if (room == null) {
            return ResponseEntity.notFound().build();
        }
        if (!userId.equals(room.getHostUserId())) {
            return ResponseEntity.status(403).build();
        }
        roomService.deleteRoom(roomId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 部屋情報取得エンドポイント
     * GET /api/rooms/{roomId}
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponse> getRoom(@PathVariable("roomId") String roomId) {
        RoomResponse room = roomService.getRoomById(UUID.fromString(roomId))
            .orElseThrow(() -> new RuntimeException("部屋が見つかりません"));
        return ResponseEntity.ok(room);
    }
}
