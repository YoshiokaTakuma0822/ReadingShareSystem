package com.readingshare.chat.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.readingshare.chat.dto.ChatStreamItemDto;

@RestController
public class ChatStreamController {
    private final ChatController chatController;

    /**
     * コンストラクタ。
     *
     * @param chatController チャットコントローラー
     */
    public ChatStreamController(ChatController chatController) {
        this.chatController = chatController;
    }

    /**
     * 指定されたルームIDのチャットストリームを取得します。
     *
     * @param roomId チャットルームのID
     * @return チャットストリームのリストを含むResponseEntity
     */
    @GetMapping("/api/rooms/{roomId}/stream")
    public ResponseEntity<List<ChatStreamItemDto>> getChatStreamAlias(@PathVariable UUID roomId) {
        return chatController.getChatStream(roomId);
    }
}
