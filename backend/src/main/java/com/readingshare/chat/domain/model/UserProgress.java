package com.readingshare.chat.domain.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * ユーザーの読書進捗エンティティ。
 */
@Entity
@Table(name = "user_progress")
public class UserProgress {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id; // 進捗情報ID (主キー)

    @NotNull
    @Column(name = "room_id", nullable = false, columnDefinition = "UUID")
    private UUID roomId; // 関連する部屋ID

    @NotNull
    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    private UUID userId; // 進捗を記録するユーザーID

    @NotNull
    @Min(0)
    @Column(name = "current_page", nullable = false)
    private int currentPage; // 現在のページ数

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt; // 進捗更新日時

    // デフォルトコンストラクタ
    public UserProgress() {
    }

    public UserProgress(UUID roomId, UUID userId, int currentPage, Instant updatedAt) {
        this.id = UUID.randomUUID();
        this.roomId = roomId;
        this.userId = userId;
        this.currentPage = currentPage;
        this.updatedAt = updatedAt;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public UUID getRoomId() {
        return roomId;
    }

    public UUID getUserId() {
        return userId;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setId(UUID id) {
        this.id = id;
    }

    public void setRoomId(UUID roomId) {
        this.roomId = roomId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "UserProgress{" +
                "id=" + id +
                ", roomId=" + roomId +
                ", userId=" + userId +
                ", currentPage=" + currentPage +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
