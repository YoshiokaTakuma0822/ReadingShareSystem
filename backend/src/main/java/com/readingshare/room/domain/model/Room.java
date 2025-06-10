package com.readingshare.room.domain.model;

import java.util.UUID;

import com.readingshare.chat.domain.model.Message;

/**
 * Domain model representing a Chat Room aggregate.
 * Encapsulates room creation logic and invariants.
 */
public class Room {
    private Long id;
    private String name;
    private String description;
    private final UUID createdBy;

    private Room(Long id, String name, String description, UUID createdBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
        validate();
    }

    /**
     * Factory method for creating a new Room.
     */
    public static Room create(String name, String description, UUID createdBy) {
        return new Room(null, name, description, createdBy);
    }

    /**
     * Reconstitute an existing Room from persistence.
     */
    public static Room reconstitute(Long id, String name, String description, UUID createdBy) {
        return new Room(id, name, description, createdBy);
    }

    private void validate() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Room name must not be blank");
        }
        if (createdBy == null) {
            throw new IllegalArgumentException("CreatedBy must not be null");
        }
    }

    /**
     * Create a chat message in this room.
     */
    public Message createMessage(String content, Member sender) {
        if (sender == null) {
            throw new IllegalArgumentException("Sender must not be null");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Message content must not be blank");
        }
        return Message.create(content, sender, this, Message.MessageType.CHAT);
    }

    /**
     * Create a join message for this room.
     */
    public Message createJoinMessage(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member must not be null");
        }
        String content = member.getName() + " joined";
        return Message.create(content, member, this, Message.MessageType.JOIN);
    }

    /**
     * Create a leave message for this room.
     */
    public Message createLeaveMessage(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member must not be null");
        }
        String content = member.getName() + " left";
        return Message.create(content, member, this, Message.MessageType.LEAVE);
    }

    // Business behaviors (e.g., rename) can be added here

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }
}
