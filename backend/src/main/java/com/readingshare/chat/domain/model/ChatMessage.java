package com.readingshare.chat.domain.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * グループチャットのメッセージエンティティ。
 *
 * @author 02001
 * @componentId C4
 * @moduleName チャットメッセージエンティティ
 * @see MessageContent
 */
@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id; // メッセージID (主キー)

    @NotNull
    @Column(name = "room_id", nullable = false, columnDefinition = "UUID")
    private UUID roomId; // 関連する部屋ID

    @NotNull
    @Column(name = "sender_user_id", nullable = false, columnDefinition = "UUID")
    private UUID senderUserId; // 送信ユーザーID

    @Embedded // MessageContentを埋め込み可能オブジェクトとして扱う
    private MessageContent content; // メッセージ内容

    @NotNull
    @Column(name = "sent_at", nullable = false)
    private Instant sentAt; // 送信日時

    /**
     * デフォルトコンストラクタ。
     * JPAの要件により必要です。
     */
    public ChatMessage() {
    }

    /**
     * 部屋ID、送信ユーザーID、メッセージ内容、送信日時を指定してチャットメッセージを作成します。
     *
     * @param roomId       関連する部屋ID
     * @param senderUserId 送信ユーザーID
     * @param content      メッセージ内容
     * @param sentAt       送信日時
     */
    public ChatMessage(UUID roomId, UUID senderUserId, MessageContent content, Instant sentAt) {
        this.id = UUID.randomUUID();
        this.roomId = roomId;
        this.senderUserId = senderUserId;
        this.content = content;
        this.sentAt = sentAt;
    }

    // --- Getter / Setter ---
    /**
     * メッセージIDを取得します。
     *
     * @return メッセージID
     */
    public UUID getId() {
        return id;
    }

    /**
     * メッセージIDを設定します。
     *
     * @param id メッセージID
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * 部屋IDを取得します。
     *
     * @return 部屋ID
     */
    public UUID getRoomId() {
        return roomId;
    }

    /**
     * 部屋IDを設定します。
     *
     * @param roomId 部屋ID
     */
    public void setRoomId(UUID roomId) {
        this.roomId = roomId;
    }

    /**
     * 送信ユーザーIDを取得します。
     *
     * @return 送信ユーザーID
     */
    public UUID getSenderUserId() {
        return senderUserId;
    }

    /**
     * 送信ユーザーIDを設定します。
     *
     * @param senderUserId 送信ユーザーID
     */
    public void setSenderUserId(UUID senderUserId) {
        this.senderUserId = senderUserId;
    }

    /**
     * メッセージ内容を取得します。
     *
     * @return メッセージ内容
     */
    public MessageContent getContent() {
        return content;
    }

    /**
     * メッセージ内容を設定します。
     *
     * @param content メッセージ内容
     */
    public void setContent(MessageContent content) {
        this.content = content;
    }

    /**
     * 送信日時を取得します。
     *
     * @return 送信日時
     */
    public Instant getSentAt() {
        return sentAt;
    }

    /**
     * 送信日時を設定します。
     *
     * @param sentAt 送信日時
     */
    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    /**
     * ChatMessageオブジェクトの文字列表現を返します。
     *
     * @return ChatMessageオブジェクトの文字列表現
     */
    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", roomId=" + roomId +
                ", senderUserId=" + senderUserId +
                ", content=" + content +
                ", sentAt=" + sentAt +
                '}';
    }
}
