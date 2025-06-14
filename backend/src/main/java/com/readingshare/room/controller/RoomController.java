package com.readingshare.room.controller;

import com.readingshare.room.service.CreateRoomService;
import com.readingshare.room.service.JoinRoomService;
import com.readingshare.room.service.SearchRoomService;
import com.readingshare.room.service.dto.CreateRoomRequest;
import com.readingshare.room.service.dto.JoinRoomRequest;
import com.readingshare.room.domain.model.Room;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部屋作成・部屋参加に関するAPIを処理するコントローラー。
 * 担当: 芳岡
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final CreateRoomService createRoomService;
    private final JoinRoomService joinRoomService;
    private final SearchRoomService searchRoomService;

    public RoomController(CreateRoomService createRoomService, JoinRoomService joinRoomService, SearchRoomService searchRoomService) {
        this.createRoomService = createRoomService;
        this.joinRoomService = joinRoomService;
        this.searchRoomService = searchRoomService;
    }

    /**
     * 新しい部屋を作成する。
     * @param request 部屋作成リクエストデータ
     * @return 作成された部屋の情報
     */
    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody CreateRoomRequest request) {
        // TODO: userIdは認証情報から取得する
        Long currentUserId = 1L; // 仮のユーザーID
        Room newRoom = createRoomService.createRoom(request.getRoomName(), request.getBookTitle(), currentUserId);
        return ResponseEntity.ok(newRoom);
    }

    /**
     * 既存の部屋に参加する。
     * @param roomId 参加する部屋のID
     * @param request 部屋参加リクエストデータ
     * @return 参加成功時はHTTP 200 OK
     */
    @PostMapping("/{roomId}/join")
    public ResponseEntity<String> joinRoom(@PathVariable Long roomId, @RequestBody JoinRoomRequest request) {
        // TODO: userIdは認証情報から取得する
        Long currentUserId = 1L; // 仮のユーザーID
        joinRoomService.joinRoom(roomId, currentUserId, request.getRoomPassword());
        return ResponseEntity.ok("Successfully joined room.");
    }

    /**
     * 部屋を検索する。
     * @param keyword 検索キーワード（部屋名、本タイトルなど）
     * @return 検索結果の部屋リスト
     */
    @GetMapping("/search")
    public ResponseEntity<List<Room>> searchRooms(@RequestParam(required = false) String keyword) {
        List<Room> rooms = searchRoomService.searchRooms(keyword);
        return ResponseEntity.ok(rooms);
    }

    /**
     * 特定の部屋の情報を取得する。
     * @param roomId 部屋ID
     * @return 部屋の情報
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long roomId) {
        return searchRoomService.getRoomById(roomId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}