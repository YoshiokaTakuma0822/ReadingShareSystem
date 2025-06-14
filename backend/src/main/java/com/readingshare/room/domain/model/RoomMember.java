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
@Table(name = "room_members")
public class RoomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomMemberId;

    @Column(nullable = false)
    private Long roomId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime joinedAt;

    // --- コンストラクタ ---
    public RoomMember() {
        // JPA用のデフォルトコンストラクタ
    }

    public RoomMember(Long roomId, Long userId) {
        this.roomId = roomId;
        this.userId = userId;
        this.joinedAt = LocalDateTime.now();
    }

    public RoomMember(Long roomMemberId, Long roomId, Long userId, LocalDateTime joinedAt) {
        this.roomMemberId = roomMemberId;
        this.roomId = roomId;
        this.userId = userId;
        this.joinedAt = joinedAt;
    }

    // --- Getter / Setter ---
    public Long getRoomMemberId() {
        return roomMemberId;
    }

    public void setRoomMemberId(Long roomMemberId) {
        this.roomMemberId = roomMemberId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    // --- equals / hashCode / toString ---
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RoomMember))
            return false;
        RoomMember that = (RoomMember) o;
        return Objects.equals(roomMemberId, that.roomMemberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomMemberId);
    }

    @Override
    public String toString() {
        return "RoomMember{" +
                "roomMemberId=" + roomMemberId +
                ", roomId=" + roomId +
                ", userId=" + userId +
                ", joinedAt=" + joinedAt +
                '}';
    }
}
