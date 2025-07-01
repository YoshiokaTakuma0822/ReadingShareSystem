package com.readingshare.chat.service;

import org.springframework.stereotype.Service;

import com.readingshare.auth.domain.repository.IUserRepository;
import com.readingshare.chat.domain.model.ChatMessage;
import com.readingshare.chat.dto.ChatMessageDto;
import com.readingshare.chat.websocket.NotificationWebSocketHandler;

@Service
public class ChatMessageBroadcastService {
    private final IUserRepository userRepository;
    private final NotificationWebSocketHandler notificationHandler;

    public ChatMessageBroadcastService(IUserRepository userRepository,
            NotificationWebSocketHandler notificationHandler) {
        this.userRepository = userRepository;
        this.notificationHandler = notificationHandler;
    }

    public void broadcastToRoom(String roomId, ChatMessage chatMessage) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setRoomId(roomId);
        dto.setSenderId(chatMessage.getSenderUserId() != null ? chatMessage.getSenderUserId().toString() : null);
        // ユーザー名取得
        String senderName = chatMessage.getSenderUserId() != null
                ? userRepository.findById(chatMessage.getSenderUserId()).map(u -> u.getUsername()).orElse("Unknown")
                : "Anonymous";
        dto.setSenderName(senderName);
        dto.setContent(chatMessage.getContent() != null ? chatMessage.getContent().getValue() : null);
        dto.setSentAt(chatMessage.getSentAt() != null ? chatMessage.getSentAt().toString() : null);
        dto.setMessageType(chatMessage.getMessageType() != null ? chatMessage.getMessageType() : "TEXT");
        dto.setSurveyId(chatMessage.getSurveyId() != null ? chatMessage.getSurveyId().toString() : null);
        System.out.println("[WebSocket送信] " + dto);

        // ネイティブWebSocketでチャットメッセージをブロードキャスト
        notificationHandler.broadcastChatMessage(roomId, dto);
        // 新着チャット通知も送信
        notificationHandler.broadcastMessage(roomId, dto.getSentAt());
    }
}
