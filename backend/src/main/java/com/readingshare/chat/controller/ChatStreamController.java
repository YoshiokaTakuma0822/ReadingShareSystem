package com.readingshare.chat.controller;

import com.readingshare.chat.dto.ChatStreamItemDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class ChatStreamController {
    private final ChatController chatController;

    public ChatStreamController(ChatController chatController) {
        this.chatController = chatController;
    }

    @GetMapping("/api/rooms/{roomId}/stream")
    public ResponseEntity<List<ChatStreamItemDto>> getChatStreamAlias(@PathVariable UUID roomId) {
        return chatController.getChatStream(roomId);
    }
}
