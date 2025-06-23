package com.readingshare.chat.service;

import com.readingshare.chat.domain.model.ChatMessage;
import com.readingshare.chat.dto.ChatMessageDto;
import com.readingshare.auth.domain.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class ChatMessageBroadcastService {
    private final SimpMessagingTemplate messagingTemplate;
    private final IUserRepository userRepository;

    @Autowired
    public ChatMessageBroadcastService(SimpMessagingTemplate messagingTemplate, IUserRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
    }

    public void broadcastToRoom(String roomId, ChatMessage chatMessage) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setRoomId(roomId);
        dto.setSenderId(chatMessage.getSenderUserId() != null ? chatMessage.getSenderUserId().toString() : null);
        // ユーザー名取得
        String senderName = chatMessage.getSenderUserId() != null ? userRepository.findById(chatMessage.getSenderUserId()).map(u -> u.getUsername()).orElse("Unknown") : "Anonymous";
        dto.setSenderName(senderName);
        dto.setContent(chatMessage.getContent() != null ? chatMessage.getContent().getValue() : null);
        dto.setSentAt(chatMessage.getSentAt() != null ? chatMessage.getSentAt().toString() : null);
        System.out.println("[WebSocket送信] " + dto);
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, dto);
    }
}
