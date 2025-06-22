package com.readingshare.chat.dto;

import java.time.Instant;
import java.util.UUID;

public class ChatMessageResponse {
    private UUID id;
    private UUID roomId;
    private UUID senderUserId;
    private String senderUsername;
    private String content;
    private Instant sentAt;

    public ChatMessageResponse(UUID id, UUID roomId, UUID senderUserId, String senderUsername, String content, Instant sentAt) {
        this.id = id;
        this.roomId = roomId;
        this.senderUserId = senderUserId;
        this.senderUsername = senderUsername;
        this.content = content;
        this.sentAt = sentAt;
    }

    public UUID getId() { return id; }
    public UUID getRoomId() { return roomId; }
    public UUID getSenderUserId() { return senderUserId; }
    public String getSenderUsername() { return senderUsername; }
    public String getContent() { return content; }
    public Instant getSentAt() { return sentAt; }
}
