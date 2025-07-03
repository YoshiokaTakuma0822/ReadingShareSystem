package com.readingshare.state.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readingshare.chat.websocket.NotificationWebSocketHandler;
import com.readingshare.state.domain.model.RoomReadingState;
import com.readingshare.state.domain.model.UserReadingState;
import com.readingshare.state.dto.RoomReadingStateResponse;
import com.readingshare.state.dto.UpdateUserReadingStateRequest;
import com.readingshare.state.dto.UserReadingStateResponse;
import com.readingshare.state.service.RoomReadingStateService;

@RestController
@RequestMapping("/api/rooms/{roomId}/states")
public class RoomReadingStateController {
    private final RoomReadingStateService service;
    private final NotificationWebSocketHandler notificationHandler;

    public RoomReadingStateController(RoomReadingStateService service,
            NotificationWebSocketHandler notificationHandler) {
        this.service = service;
        this.notificationHandler = notificationHandler;
    }

    @PostMapping("/{memberId}")
    public ResponseEntity<Void> updateUserReadingState(
            @PathVariable String roomId,
            @RequestBody UpdateUserReadingStateRequest request) {
        UserReadingState userState = new UserReadingState(
                request.userId(),
                request.currentPage(),
                request.comment());
        service.updateUserReadingState(roomId, userState);
        // Broadcast reading progress via WebSocket
        notificationHandler.broadcastProgress(
                roomId,
                0,
                String.valueOf(request.currentPage()));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<RoomReadingStateResponse> getRoomReadingState(@PathVariable String roomId) {
        RoomReadingState state = service.getRoomReadingState(roomId);
        if (state == null) {
            return ResponseEntity.notFound().build();
        }

        RoomReadingStateResponse response = new RoomReadingStateResponse(
                state.getRoomId(),
                state.getAllUserStates().stream()
                        .map(userState -> new UserReadingStateResponse(
                                userState.getUserId(),
                                userState.getCurrentPage(),
                                userState.getComment()))
                        .toList());

        return ResponseEntity.ok(response);
    }
}
