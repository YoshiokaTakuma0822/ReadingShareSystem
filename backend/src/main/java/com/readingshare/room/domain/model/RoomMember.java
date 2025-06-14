package com.readingshare.room.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

/**
 * F3 部屋参加情報に基く部屋のメンバー情報エンティティ。
 */
@Entity
@Table(name = "room_members")
public class RoomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 参加情報ID (主キー)

    @NotNull
    @Column(name = "room_id", nullable = false)
    private Long roomId; // F2 部屋情報と連携する部屋ID

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId; // F1 会員情報と連携するユーザーID

    @NotNull
    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt; // 参加日時

    // デフォルトコンストラクタ
    public RoomMember() {}

    public RoomMember(Long roomId, Long userId, Instant joinedAt) {
        this.roomId = roomId;
        this.userId = userId;
        this.joinedAt = joinedAt;
    }

    // Getter
    public Long getId() { return id; }
    public Long getRoomId() { return roomId; }
    public Long getUserId() { return userId; }
    public Instant getJoinedAt() { return joinedAt; }

    // Setter (JPAのためにidは必要)
    public void setId(Long id) { this.id = id; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setJoinedAt(Instant joinedAt) { this.joinedAt = joinedAt; }
}