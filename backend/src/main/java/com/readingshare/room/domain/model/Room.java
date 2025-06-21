package com.readingshare.room.domain.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

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

    private Integer totalPages;      // 本のページ数
    private Integer pageTurnSpeed;   // ページめくり速度

    // --- フィールド追加 ---
    private String genre;
    private String startTime;
    private String endTime;

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

    public Room(String roomName, Long hostUserId, Integer totalPages, Integer pageTurnSpeed) {
        this.roomName = roomName;
        this.hostUserId = hostUserId;
        this.createdAt = LocalDateTime.now();
        this.totalPages = totalPages;
        this.pageTurnSpeed = pageTurnSpeed;
    }

    public Room(String roomName, Long hostUserId, Integer totalPages, Integer pageTurnSpeed, String genre, String startTime, String endTime) {
        this.roomName = roomName;
        this.hostUserId = hostUserId;
        this.createdAt = LocalDateTime.now();
        this.totalPages = totalPages;
        this.pageTurnSpeed = pageTurnSpeed;
        this.genre = genre;
        this.startTime = startTime;
        this.endTime = endTime;
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

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getPageTurnSpeed() {
        return pageTurnSpeed;
    }

    public void setPageTurnSpeed(Integer pageTurnSpeed) {
        this.pageTurnSpeed = pageTurnSpeed;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    // --- equals / hashCode / toString ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
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
                ", totalPages=" + totalPages +
                ", pageTurnSpeed=" + pageTurnSpeed +
                ", genre='" + genre + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
