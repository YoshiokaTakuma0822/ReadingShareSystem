package com.readingshare.room.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(nullable = false, length = 100)
    private String roomName;

    @Column(nullable = false, length = 200)
    private String bookTitle;

    @Column(nullable = false, columnDefinition = "UUID")
    private UUID hostUserId;

    @Column(nullable = false)
    private Instant createdAt;

    @JsonIgnore
    @Column(nullable = true)
    private String roomPasswordHash;

    // --- コンストラクタ ---
    public Room() {
        // JPA用
    }

    public Room(String roomName, String bookTitle, UUID hostUserId) {
        this.id = UUID.randomUUID();
        this.roomName = roomName;
        this.bookTitle = bookTitle;
        this.hostUserId = hostUserId;
        this.createdAt = Instant.now();
    }

    public Room(UUID id, String roomName, String bookTitle, UUID hostUserId, Instant createdAt) {
        this.id = id;
        this.roomName = roomName;
        this.bookTitle = bookTitle;
        this.hostUserId = hostUserId;
        this.createdAt = createdAt;
    }

    // --- ゲッター ---
    public UUID getId() {
        return id;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public UUID getHostUserId() {
        return hostUserId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getRoomPasswordHash() {
        return roomPasswordHash;
    }

    /**
     * パスワードが設定されているかどうかを判定する。
     * JSONシリアライゼーション時にクライアントに返される。
     *
     * @return パスワードが設定されている場合はtrue、そうでなければfalse
     */
    public boolean isHasPassword() {
        return roomPasswordHash != null && !roomPasswordHash.isEmpty();
    }

    // --- セッター ---
    public void setRoomPasswordHash(String roomPasswordHash) {
        this.roomPasswordHash = roomPasswordHash;
    }

    // --- その他のメソッド ---
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Room room = (Room) o;
        return Objects.equals(id, room.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", roomName='" + roomName + '\'' +
                ", bookTitle='" + bookTitle + '\'' +
                ", hostUserId=" + hostUserId +
                ", createdAt=" + createdAt +
                '}';
    }
}
