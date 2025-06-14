package com.readingshare.chat.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

/**
 * チャットメッセージエンティティ。
 */
@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // メッセージID (主キー)

    @NotNull
    @Column(name = "room_id", nullable = false)
    private Long roomId; // 関連する部屋ID

    @NotNull
    @Column(name = "sender_user_id", nullable = false)
    private Long senderUserId; // 送信ユーザーID

    @Embedded // MessageContentを埋め込み可能オブジェクトとして扱う
    private MessageContent content; // メッセージ内容

    @NotNull
    @Column(name = "sent_at", nullable = false)
    private Instant sentAt; // 送信日時

    // デフォルトコンストラクタ
    public ChatMessage() {}

    public ChatMessage(Long id, Long roomId, Long senderUserId, MessageContent content, Instant sentAt) {
        this.id = id;
        this.roomId = roomId;
        this.senderUserId = senderUserId;
        this.content = content;
        this.sentAt = sentAt;
    }

    // Getters
    public Long getId() { return id; }
    public Long getRoomId() { return roomId; }
    public Long getSenderUserId() { return senderUserId; }
    public MessageContent getContent() { return content; }
    public Instant getSentAt() { return sentAt; }

    // Setters (JPAのためにidは必要)
    public void setId(Long id) { this.id = id; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public void setSenderUserId(Long senderUserId) { this.senderUserId = senderUserId; }
    public void setContent(MessageContent content) { this.content = content; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }
}