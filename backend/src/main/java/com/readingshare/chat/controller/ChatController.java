package com.readingshare.chat.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readingshare.chat.domain.model.ChatMessage;
import com.readingshare.chat.dto.SendMessageRequest;
import com.readingshare.chat.service.GetChatHistoryService;
import com.readingshare.chat.service.SendChatMessageService;

/**
 * グループチャットに関するAPIを処理するコントローラー。
 * 担当: 榎本
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final SendChatMessageService sendChatMessageService;
    private final GetChatHistoryService getChatHistoryService;

    public ChatController(SendChatMessageService sendChatMessageService, GetChatHistoryService getChatHistoryService) {
        this.sendChatMessageService = sendChatMessageService;
        this.getChatHistoryService = getChatHistoryService;
    }

    /**
     * チャットメッセージを送信する。
     * 
     * @param roomId  メッセージを送信する部屋のID
     * @param request 送信メッセージリクエスト
     * @return 送信成功時はHTTP 200 OK
     */
    @PostMapping("/{roomId}/message")
    public ResponseEntity<String> sendMessage(@PathVariable Long roomId, @RequestBody SendMessageRequest request) {
        // TODO: userIdは認証情報から取得する
        Long currentUserId = 1L; // 仮のユーザーID
        sendChatMessageService.sendMessage(roomId, currentUserId, request.getMessageContent());
        return ResponseEntity.ok("Message sent successfully.");
    }

    /**
     * 特定の部屋のチャット履歴を取得する。
     * 
     * @param roomId 履歴を取得する部屋のID
     * @return チャットメッセージのリスト
     */
    @GetMapping("/{roomId}/history")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable Long roomId) {
        List<ChatMessage> messages = getChatHistoryService.getChatHistory(roomId);
        return ResponseEntity.ok(messages);
    }
}
