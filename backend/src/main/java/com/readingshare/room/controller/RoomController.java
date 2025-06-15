package com.readingshare.room.controller;

import java.util.List;
import java.util.UUID;

import org.jspecify.annotations.NonNull;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.readingshare.room.domain.model.Room;
import com.readingshare.room.dto.RoomResponse;
import com.readingshare.room.service.RoomService;
import com.readingshare.security.JwtPayload;

/**
 * Controller for room-related functionality.
 * Handles room operations. Message operations have been moved to
 * MessageController.
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    /**
     * Get all available rooms.
     *
     * @return list of all rooms
     */
    @GetMapping
    @ResponseBody
    public @NonNull List<RoomResponse> getRooms(@AuthenticationPrincipal Jwt jwt) {
        // Authentication verified by JWT security
        return roomService.getAllRooms();
    }

    /**
     * Get a specific room by ID.
     *
     * @param roomId the room ID
     * @return the room if found
     * @throws RuntimeException if room not found
     */
    @GetMapping("/{roomId}")
    @ResponseBody
    public @NonNull RoomResponse getRoomById(@PathVariable Long roomId, @AuthenticationPrincipal Jwt jwt) {
        // Authentication verified by JWT security
        return roomService.getRoomById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found: " + roomId));
    }

    public record CreateRoomRequest(@NonNull String name, String description) {
    }

    /**
     * Create a new room.
     *
     * Note: attempting to create a room with a duplicate name will result in an
     * error (e.g., 409 Conflict). You can use GET /api/rooms or GET
     * /api/rooms/{roomId} to verify existing rooms before creating.
     *
     * @param createRoomRequest the room creation request
     * @param jwt               the JWT containing authenticated user info
     * @return the created room
     */
    @PostMapping
    @ResponseBody
    public @NonNull RoomResponse createRoom(
            @RequestBody @NonNull CreateRoomRequest createRoomRequest,
            @AuthenticationPrincipal Jwt jwt) {

        // Get the authenticated user's account
        var payload = JwtPayload.fromJwt(jwt);
        UUID accountId = UUID.fromString(payload.sub());

        // Account verification is done by the service layer
        return roomService.createRoom(
                createRoomRequest.name(),
                createRoomRequest.description(),
                accountId);
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
