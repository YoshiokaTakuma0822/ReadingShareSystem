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
import com.readingshare.chat.dto.ChatMessageResponse;
import com.readingshare.chat.dto.SendMessageRequest;
import com.readingshare.chat.service.GetChatHistoryService;
import com.readingshare.chat.service.SendChatMessageService;
import com.readingshare.auth.domain.repository.IUserRepository;
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
    private final IUserRepository userRepository;

    public ChatController(SendChatMessageService sendChatMessageService, GetChatHistoryService getChatHistoryService, IUserRepository userRepository) {
        this.sendChatMessageService = sendChatMessageService;
        this.getChatHistoryService = getChatHistoryService;
        this.userRepository = userRepository;
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
        sendChatMessageService.sendMessage(roomId, currentUserId, request.messageContent());
        return ResponseEntity.ok("Message sent successfully.");
    }

    /**
     * チャット履歴を取得する。
     *
     * @param roomId 部屋のID
     * @return チャットメッセージのリスト
     */
    @GetMapping("/{roomId}/history")
    public ResponseEntity<List<ChatMessageResponse>> getChatHistory(@PathVariable UUID roomId) {
        List<ChatMessage> messages = getChatHistoryService.getChatHistory(roomId);
        List<ChatMessageResponse> response = messages.stream().map(msg -> {
            String username = userRepository.findById(msg.getSenderUserId())
                .map(u -> u.getUsername()).orElse("匿名");
            return new ChatMessageResponse(
                msg.getId(),
                msg.getRoomId(),
                msg.getSenderUserId(),
                username,
                msg.getContent().getValue(),
                msg.getSentAt()
            );
        }).toList();
        return ResponseEntity.ok(response);
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
