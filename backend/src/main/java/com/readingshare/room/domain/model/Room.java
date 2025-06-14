package com.readingshare.room.domain.model;

import java.time.LocalDateTime;
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

    @Column(nullable = false)
    private Long hostUserId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // --- コンストラクタ ---
    public Room() {
        // JPA用
    }

    public Room(String roomName, Long hostUserId) {
        this.roomName = roomName;
        this.hostUserId = hostUserId;
        this.createdAt = LocalDateTime.now();
    }

    public Room(Long roomId, String roomName, Long hostUserId, LocalDateTime createdAt) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.hostUserId = hostUserId;
        this.createdAt = createdAt;
    }

    // --- Getter / Setter ---
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

    public Long getHostUserId() {
        return hostUserId;
    }

    public void setHostUserId(Long hostUserId) {
        this.hostUserId = hostUserId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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
                ", hostUserId=" + hostUserId +
                ", createdAt=" + createdAt +
                '}';
    }
}
