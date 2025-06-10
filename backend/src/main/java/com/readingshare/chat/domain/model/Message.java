package com.readingshare.chat.domain.model;

import java.time.OffsetDateTime;

import com.readingshare.room.domain.model.Member;
import com.readingshare.room.domain.model.Room;

/**
 * Message domain model.
 * Represents a chat message in the domain layer.
 */
public class Message {
    public enum MessageType {
        CHAT, JOIN, LEAVE
    }

    private final Long id;
    private final String content;
    private final Member sender;
    private final Room room;
    private final OffsetDateTime createdAt;
    private final MessageType type;

    private Message(Long id, String content, Member sender, Room room, OffsetDateTime createdAt, MessageType type) {
        this.id = id;
        this.content = content;
        this.sender = sender;
        this.room = room;
        this.createdAt = createdAt;
        this.type = type;
    }

    /**
     * Create a new message (for creation scenarios)
     */
    public static Message create(String content, Member sender, Room room, MessageType type) {
        return new Message(null, content, sender, room, OffsetDateTime.now(), type);
    }

    /**
     * Reconstitute a message from persistence (for loading scenarios)
     */
    public static Message reconstitute(Long id, String content, Member sender, Room room, OffsetDateTime createdAt,
            MessageType type) {
        return new Message(id, content, sender, room, createdAt, type);
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Member getSender() {
        return sender;
    }

    public Room getRoom() {
        return room;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public MessageType getType() {
        return type;
    }
}
