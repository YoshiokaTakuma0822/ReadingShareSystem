package com.readingshare.room.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "room_members")
public class RoomMember {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false, columnDefinition = "UUID")
    private UUID userId;

    @Column(nullable = false)
    private Instant joinedAt;

    // --- コンストラクタ ---
    public RoomMember() {
        // JPA用
    }

    public RoomMember(Room room, UUID userId, Instant joinedAt) {
        this.id = UUID.randomUUID();
        this.room = room;
        this.userId = userId;
        this.joinedAt = joinedAt;
    }

    // --- Getter / Setter ---
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }

    // --- その他のメソッド ---
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RoomMember that = (RoomMember) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RoomMember{" +
                "id=" + id +
                ", room=" + (room != null ? room.getId() : null) +
                ", userId=" + userId +
                ", joinedAt=" + joinedAt +
                '}';
    }
}
