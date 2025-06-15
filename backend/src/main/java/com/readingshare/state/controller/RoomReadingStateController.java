package com.readingshare.state.controller;

import com.readingshare.state.domain.model.RoomReadingState;
import com.readingshare.state.domain.model.UserReadingState;
import com.readingshare.state.service.RoomReadingStateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/room-reading-state")
public class RoomReadingStateController {
    private final RoomReadingStateService service;

    public RoomReadingStateController(RoomReadingStateService service) {
        this.service = service;
    }

    @PostMapping("/{roomId}/user")
    public ResponseEntity<Void> updateUserReadingState(
            @PathVariable String roomId,
            @RequestBody UserReadingState userState) {
        service.updateUserReadingState(roomId, userState);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomReadingState> getRoomReadingState(@PathVariable String roomId) {
        RoomReadingState state = service.getRoomReadingState(roomId);
        if (state == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(state);
    }

    @PostMapping("/{roomId}/user/turn")
    public ResponseEntity<UserReadingState> turnPage(
            @PathVariable String roomId,
            @RequestParam String userId,
            @RequestParam String direction) {
        UserReadingState updated = service.turnPage(roomId, userId, direction);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{roomId}/user/auto-turn")
    public ResponseEntity<UserReadingState> autoTurnPage(
            @PathVariable String roomId,
            @RequestParam String userId) {
        UserReadingState updated = service.autoTurnPage(roomId, userId);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }
}
