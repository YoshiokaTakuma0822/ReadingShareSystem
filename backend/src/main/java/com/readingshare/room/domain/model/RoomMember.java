package com.readingshare.room.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * ルームメンバー（部屋の参加者）を表すエンティティ。部屋立て・部屋参加機能の一部として、部屋に参加しているユーザーの情報を管理します。
 *
 * @author 02004
 * @componentId C3
 * @moduleName ルームメンバードメインモデル
 */
@Entity
@Table(name = "room_members")
public class RoomMember {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id; // メンバーID

    @Column(nullable = false, columnDefinition = "UUID")
    private UUID roomId; // 部屋ID

    @Column(nullable = false, columnDefinition = "UUID")
    private UUID userId; // ユーザーID

    @Column(nullable = false)
    private Instant joinedAt; // 参加日時

    /**
     * デフォルトコンストラクタ。
     * JPAの要件により必要です。
     */
    public RoomMember() {
        // JPA用
    }

    /**
     * 部屋ID、ユーザーID、参加日時を指定してRoomMemberを作成します。
     *
     * @param roomId   部屋ID
     * @param userId   ユーザーID
     * @param joinedAt 参加日時
     */
    public RoomMember(UUID roomId, UUID userId, Instant joinedAt) {
        this.id = UUID.randomUUID();
        this.roomId = roomId;
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

    public UUID getRoomId() {
        return roomId;
    }

    public void setRoomId(UUID roomId) {
        this.roomId = roomId;
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
                ", roomId=" + roomId +
                ", userId=" + userId +
                ", joinedAt=" + joinedAt +
                '}';
    }
}
