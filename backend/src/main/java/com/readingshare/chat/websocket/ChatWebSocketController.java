package com.readingshare.chat.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

// WebSocket用のDTO
class ChatMessageDTO {
    private String roomId;
    private String senderId;
    private String senderName;
    private String content;
    private String sentAt;

    // getter/setter
    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }
}

@Controller
public class ChatWebSocketController {
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessageDTO sendMessage(@Payload ChatMessageDTO chatMessage) {
        System.out.println("[WebSocket受信] content=" + chatMessage.getContent() + ", roomId=" + chatMessage.getRoomId()
                + ", sender=" + chatMessage.getSenderName());
        // 追加: すべてのフィールドを出力
        System.out.println("[WebSocket受信:全フィールド] " +
                "roomId=" + chatMessage.getRoomId() +
                ", senderId=" + chatMessage.getSenderId() +
                ", senderName=" + chatMessage.getSenderName() +
                ", content=" + chatMessage.getContent() +
                ", sentAt=" + chatMessage.getSentAt());
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessageDTO addUser(@Payload ChatMessageDTO chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        java.util.Map<String, Object> sessionAttrs = headerAccessor.getSessionAttributes();
        if (sessionAttrs != null) {
            sessionAttrs.put("username", chatMessage.getSenderName());
        }
        return chatMessage;
    }
}
