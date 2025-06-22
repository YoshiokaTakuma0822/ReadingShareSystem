package com.readingshare.room.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
 * ルーム作成および参加のREST APIコントローラ。HTTPリクエストを受け取り、部屋生成・参加処理を提供する。
 *
 * @author 02004
 * @componentId C3
 * @moduleName Roomコントローラ
 * @see RoomService
 * @see CreateRoomRequest
 * @see JoinRoomRequest
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
     *
     * 部屋を作成します。
     *
     * @param request 部屋作成リクエスト
     * @return 作成された部屋の情報
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
     *
     * 部屋の一覧を取得します。
     *
     * @param limit 取得する部屋の最大数
     * @return 部屋のリスト
     */
    @GetMapping
    public ResponseEntity<List<Room>> getRooms(@RequestParam(value = "limit", defaultValue = "10") int limit) {
        List<Room> rooms = roomService.getRooms(limit);
        return ResponseEntity.ok(rooms);
    }

    /**
     * 部屋参加エンドポイント
     * POST /api/rooms/join
     *
     * 部屋に参加します。
     *
     * @param request 部屋参加リクエスト
     * @return 参加した部屋のメンバー情報
     */
    @PostMapping("/join")
    public ResponseEntity<RoomMember> joinRoom(@RequestBody JoinRoomRequest request) {
        RoomMember roomMember = roomService.joinRoom(request.roomId(), request.userId(), request.roomPassword());
        return ResponseEntity.ok(roomMember);
    }

    /**
     * 部屋検索エンドポイント
     * GET /api/rooms/search?keyword=xxx
     *
     * キーワードで部屋を検索します。
     *
     * @param keyword 検索キーワード
     * @return 検索結果の部屋リスト
     */
    @GetMapping("/search")
    public ResponseEntity<List<Room>> searchRooms(@RequestParam String keyword) {
        List<Room> rooms = roomService.searchRooms(keyword);
        return ResponseEntity.ok(rooms);
    }
}
