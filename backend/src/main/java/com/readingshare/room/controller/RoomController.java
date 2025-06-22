package com.readingshare.room.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.model.RoomMember;
import com.readingshare.room.dto.CreateRoomRequest;
import com.readingshare.room.dto.JoinRoomRequest;
import com.readingshare.room.service.RoomService;

/**
 * REST API コントローラー - 部屋作成 / 参加 / 検索
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    /**
     * 部屋作成エンドポイント
     * POST /api/rooms
     */
    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody CreateRoomRequest request) {
        Room createdRoom = request.password() != null
                ? roomService.createRoomWithPassword(request.roomName(), request.bookTitle(),
                        request.hostUserId(), request.password())
                : roomService.createRoom(request.roomName(), request.bookTitle(), request.hostUserId());
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
}
