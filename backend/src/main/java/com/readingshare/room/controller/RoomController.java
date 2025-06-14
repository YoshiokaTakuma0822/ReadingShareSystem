package com.readingshare.room.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.model.RoomMember;
import com.readingshare.room.service.CreateRoomService;
import com.readingshare.room.service.JoinRoomService;
import com.readingshare.room.service.SearchRoomService;
import com.readingshare.room.service.dto.CreateRoomRequest;
import com.readingshare.room.service.dto.JoinRoomRequest;

/**
 * REST API コントローラー - 部屋作成 / 参加 / 検索
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final CreateRoomService createRoomService;
    private final JoinRoomService joinRoomService;
    private final SearchRoomService searchRoomService;

    @Autowired
    public RoomController(
            CreateRoomService createRoomService,
            JoinRoomService joinRoomService,
            SearchRoomService searchRoomService) {
        this.createRoomService = createRoomService;
        this.joinRoomService = joinRoomService;
        this.searchRoomService = searchRoomService;
    }

    /**
     * 部屋作成エンドポイント
     * POST /api/rooms
     */
    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody CreateRoomRequest request) {
        Room createdRoom = createRoomService.createRoom(request);
        return ResponseEntity.ok(createdRoom);
    }

    /**
     * 部屋参加エンドポイント
     * POST /api/rooms/join
     */
    @PostMapping("/join")
    public ResponseEntity<RoomMember> joinRoom(@RequestBody JoinRoomRequest request) {
        RoomMember joinedMember = joinRoomService.joinRoom(request);
        return ResponseEntity.ok(joinedMember);
    }

    /**
     * 部屋検索エンドポイント
     * GET /api/rooms/search?keyword=xxx
     */
    @GetMapping("/search")
    public ResponseEntity<List<Room>> searchRooms(@RequestParam String keyword) {
        List<Room> rooms = searchRoomService.searchRooms(keyword);
        return ResponseEntity.ok(rooms);
    }
}
