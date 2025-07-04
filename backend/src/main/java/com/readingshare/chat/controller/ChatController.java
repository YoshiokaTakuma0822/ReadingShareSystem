package com.readingshare.chat.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readingshare.auth.domain.repository.IUserRepository;
import com.readingshare.auth.infrastructure.security.UserPrincipal;
import com.readingshare.chat.domain.model.ChatMessage;
import com.readingshare.chat.dto.ChatMessageDto;
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
    private final IUserRepository userRepository;

    public ChatController(SendChatMessageService sendChatMessageService, GetChatHistoryService getChatHistoryService,
            ChatMessageBroadcastService chatMessageBroadcastService, IUserRepository userRepository) {
        this.sendChatMessageService = sendChatMessageService;
        this.getChatHistoryService = getChatHistoryService;
        this.chatMessageBroadcastService = chatMessageBroadcastService;
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
        ChatMessage chatMessage = sendChatMessageService.sendMessage(roomId, currentUserId, request.messageContent());
        // REST経由の送信でもWebSocketでブロードキャスト
        chatMessageBroadcastService.broadcastToRoom(roomId.toString(), chatMessage);
        return ResponseEntity.ok("Message sent successfully.");
    }

    /**
     * チャット履歴を取得する。
     *
     * @param roomId 部屋のID
     * @return チャットメッセージのリスト（DTO形式）
     */
    @GetMapping("/{roomId}/history")
    public ResponseEntity<List<ChatMessageDto>> getChatHistory(@PathVariable UUID roomId) {
        List<ChatMessage> chatHistory = getChatHistoryService.getChatHistory(roomId);
        List<ChatMessageDto> chatHistoryDto = chatHistory.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(chatHistoryDto);
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

    /**
     * ChatMessageエンティティをChatMessageDTOに変換する。
     *
     * @param chatMessage 変換元のエンティティ
     * @return 変換されたDTO
     */
    private ChatMessageDto convertToDto(ChatMessage chatMessage) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setId(chatMessage.getId().toString());
        dto.setRoomId(chatMessage.getRoom() != null ? chatMessage.getRoom().getId().toString() : null);
        dto.setSenderId(chatMessage.getSenderUserId() != null ? chatMessage.getSenderUserId().toString() : null);

        // ユーザー名取得
        String senderName = chatMessage.getSenderUserId() != null
                ? userRepository.findById(chatMessage.getSenderUserId()).map(u -> u.getUsername()).orElse("Unknown")
                : "Anonymous";
        dto.setSenderName(senderName);

        dto.setContent(chatMessage.getContent() != null ? chatMessage.getContent().getValue() : null);
        dto.setSentAt(chatMessage.getSentAt() != null ? chatMessage.getSentAt().toString() : null);
        dto.setMessageType(chatMessage.getMessageType());
        dto.setSurveyId(chatMessage.getSurveyId() != null ? chatMessage.getSurveyId().toString() : null);
        return dto;
    }
}
