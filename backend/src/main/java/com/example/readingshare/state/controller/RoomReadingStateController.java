package com.example.readingshare.state.controller;

import com.example.readingshare.state.domain.model.RoomReadingState;
import com.example.readingshare.state.domain.model.UserReadingState;
import com.example.readingshare.state.service.RoomReadingStateService;
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
}
