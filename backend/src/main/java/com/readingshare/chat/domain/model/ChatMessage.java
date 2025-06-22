package com.readingshare.chat.domain.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * チャットメッセージエンティティ。
 */
@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id; // メッセージID (主キー)

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private com.readingshare.room.domain.model.Room room; // 関連する部屋

    @NotNull
    @Column(name = "sender_user_id", nullable = false, columnDefinition = "UUID")
    private UUID senderUserId; // 送信ユーザーID

    @Embedded // MessageContentを埋め込み可能オブジェクトとして扱う
    private MessageContent content; // メッセージ内容

    @NotNull
    @Column(name = "sent_at", nullable = false)
    private Instant sentAt; // 送信日時

    // デフォルトコンストラクタ
    public ChatMessage() {
    }

    public ChatMessage(com.readingshare.room.domain.model.Room room, UUID senderUserId, MessageContent content, Instant sentAt) {
        this.id = UUID.randomUUID();
        this.room = room;
        this.senderUserId = senderUserId;
        this.content = content;
        this.sentAt = sentAt;
    }

    // --- Getter / Setter ---
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public com.readingshare.room.domain.model.Room getRoom() {
        return room;
    }

    public void setRoom(com.readingshare.room.domain.model.Room room) {
        this.room = room;
    }

    public UUID getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(UUID senderUserId) {
        this.senderUserId = senderUserId;
    }

    public MessageContent getContent() {
        return content;
    }

    public void setContent(MessageContent content) {
        this.content = content;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", room=" + (room != null ? room.getId() : null) +
                ", senderUserId=" + senderUserId +
                ", content=" + content +
                ", sentAt=" + sentAt +
                '}';
    }
}
