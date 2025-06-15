package com.readingshare.room.domain.model;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(nullable = false, length = 100)
    private String roomName;

    @Column(nullable = false, length = 200)
    private String bookTitle;

    @Column(nullable = false)
    private Long hostUserId;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = true)
    private String roomPasswordHash;

    // --- コンストラクタ ---
    public Room() {
        // JPA用
    }

    public Room(String roomName, String bookTitle, Long hostUserId) {
        this.roomName = roomName;
        this.bookTitle = bookTitle;
        this.hostUserId = hostUserId;
        this.createdAt = Instant.now();
    }

    public Room(Long roomId, String roomName, String bookTitle, Long hostUserId, Instant createdAt) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.bookTitle = bookTitle;
        this.hostUserId = hostUserId;
        this.createdAt = createdAt;
    }

    // --- Getter / Setter ---
    public Long getId() {
        return roomId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public Long getHostUserId() {
        return hostUserId;
    }

    public void setHostUserId(Long hostUserId) {
        this.hostUserId = hostUserId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getRoomPasswordHash() {
        return roomPasswordHash;
    }

    public void setRoomPasswordHash(String roomPasswordHash) {
        this.roomPasswordHash = roomPasswordHash;
    }

    public boolean hasPassword() {
        return roomPasswordHash != null && !roomPasswordHash.isEmpty();
    }

    // --- equals / hashCode / toString ---
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Room))
            return false;
        Room room = (Room) o;
        return Objects.equals(roomId, room.roomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomId);
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomId=" + roomId +
                ", roomName='" + roomName + '\'' +
                ", bookTitle='" + bookTitle + '\'' +
                ", hostUserId=" + hostUserId +
                ", createdAt=" + createdAt +
                '}';
    }
}
