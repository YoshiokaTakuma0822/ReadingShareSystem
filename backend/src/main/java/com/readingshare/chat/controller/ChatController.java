package com.readingshare.chat.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readingshare.auth.infrastructure.security.UserPrincipal;
import com.readingshare.chat.domain.model.ChatMessage;
import com.readingshare.chat.dto.SendMessageRequest;
import com.readingshare.chat.service.ChatMessageBroadcastService;
import com.readingshare.chat.service.GetChatHistoryService;
import com.readingshare.chat.service.SendChatMessageService;
import com.readingshare.common.exception.ApplicationException;

/**
 * グループチャットに関するAPIを処理するコントローラー。
 * 担当: 榎本
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final SendChatMessageService sendChatMessageService;
    private final GetChatHistoryService getChatHistoryService;
    private final ChatMessageBroadcastService chatMessageBroadcastService;

    public ChatController(SendChatMessageService sendChatMessageService, GetChatHistoryService getChatHistoryService, ChatMessageBroadcastService chatMessageBroadcastService) {
        this.sendChatMessageService = sendChatMessageService;
        this.getChatHistoryService = getChatHistoryService;
        this.chatMessageBroadcastService = chatMessageBroadcastService;
    }

    /**
     * チャットメッセージを送信する。
     *
     * @param roomId  メッセージを送信する部屋のID
     * @param request 送信メッセージリクエスト
     * @return 送信成功時はHTTP 200 OK
     */
    @PostMapping("/{roomId}/message")
    public ResponseEntity<String> sendMessage(@PathVariable UUID roomId, @RequestBody SendMessageRequest request) {
        UUID currentUserId = getCurrentUserId();
        ChatMessage chatMessage = sendChatMessageService.sendMessage(roomId, currentUserId, request.messageContent());
        // REST経由の送信でもWebSocketでブロードキャスト
        chatMessageBroadcastService.broadcastToRoom(roomId.toString(), chatMessage);
        return ResponseEntity.ok("Message sent successfully.");
    }

    /**
     * チャット履歴を取得する。
     *
     * @param roomId 部屋のID
     * @return チャットメッセージのリスト
     */
    @GetMapping("/{roomId}/history")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable UUID roomId) {
        List<ChatMessage> chatHistory = getChatHistoryService.getChatHistory(roomId);
        return ResponseEntity.ok(chatHistory);
    }

    /**
     * 現在認証されているユーザーのIDを取得する。
     *
     * @return 現在のユーザーID
     * @throws ApplicationException 認証されていない場合
     */
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            return userPrincipal.getUserId();
        }

        throw new ApplicationException("User not authenticated");
    }
}
