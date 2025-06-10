package com.readingshare.chat.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.readingshare.chat.dto.MessageRequest;
import com.readingshare.chat.dto.MessageResponse;
import com.readingshare.chat.service.ChatService;
import com.readingshare.common.service.WebSocketService;
import com.readingshare.room.service.RoomService;
import com.readingshare.security.JwtPayload;

/**
 * Controller for message-related functionality within rooms.
 * Handles message operations for specific rooms.
 */
@RestController
@RequestMapping("/api/rooms/{roomId}/messages")
public class MessageController {
    private final ChatService chatService;
    private final WebSocketService webSocketService;
    private final RoomService roomService;

    public MessageController(
            ChatService chatService,
            WebSocketService webSocketService,
            RoomService roomService) {
        this.chatService = chatService;
        this.webSocketService = webSocketService;
        this.roomService = roomService;
    }

    /**
     * Handles new chat message submission for a specific room.
     *
     * @param roomId  the ID of the room to send the message to
     * @param message the chat message to send
     * @param jwt     the JWT containing user info
     * @return the created message
     */
    @PostMapping
    @ResponseBody
    public MessageResponse sendMessage(
            @PathVariable Long roomId,
            @RequestBody @NonNull MessageRequest messageRequest,
            @AuthenticationPrincipal Jwt jwt) {
        var payload = JwtPayload.fromJwt(jwt);
        UUID accountId = UUID.fromString(payload.sub());
        var member = roomService.getMemberByAccountAndRoom(accountId, roomId)
                .orElseThrow(() -> new RuntimeException("Member not found in room"));

        // Create message through ChatService using the room's domain method
        var created = chatService.createMessageInRoom(messageRequest.content(), roomId, member.id());

        // Broadcast to both global and room-specific topics for backwards compatibility
        webSocketService.broadcastMessage();
        webSocketService.broadcastMessageToRoom(roomId);
        return created;
    }

    /**
     * Fetch chat messages for a specific room. If beforeId is provided, returns
     * messages before that
     * ID.
     * If beforeId is not provided, returns the most recent messages.
     *
     * @param roomId   the ID of the room to fetch messages from
     * @param beforeId the ID to fetch messages before (optional)
     * @param limit    maximum number of messages to return (default 20, max 50)
     * @return list of messages in chronological order (oldest first for beforeId,
     *         newest first for recent)
     */
    @GetMapping
    @ResponseBody
    public @NonNull List<MessageResponse> getMessages(
            @PathVariable Long roomId,
            @RequestParam(required = false) Long beforeId,
            @RequestParam(defaultValue = "20") int limit) {

        // Verify the room exists
        roomService.getRoomById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // Enforce maximum limit of 50
        int effectiveLimit = Math.min(limit, 50);
        return chatService.getMessagesBeforeAsDto(roomId, beforeId, effectiveLimit);
    }
}
