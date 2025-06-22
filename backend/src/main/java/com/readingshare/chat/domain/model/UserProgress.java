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

    /**
     * デフォルトコンストラクタ。
     * JPAの要件により必要です。
     */
    public UserProgress() {
    }

    /**
     * 部屋ID、ユーザーID、現在のページ数、更新日時を指定してUserProgressを作成します。
     *
     * @param roomId      関連する部屋ID
     * @param userId      進捗を記録するユーザーID
     * @param currentPage 現在のページ数
     * @param updatedAt   進捗更新日時
     */
    public UserProgress(UUID roomId, UUID userId, int currentPage, Instant updatedAt) {
        this.id = UUID.randomUUID();
        this.roomId = roomId;
        this.userId = userId;
        this.currentPage = currentPage;
        this.updatedAt = updatedAt;
    }

    /**
     * 進捗情報IDを取得します。
     *
     * @return 進捗情報ID
     */
    public UUID getId() {
        return id;
    }

    /**
     * 関連する部屋IDを取得します。
     *
     * @return 部屋ID
     */
    public UUID getRoomId() {
        return roomId;
    }

    /**
     * 進捗を記録するユーザーIDを取得します。
     *
     * @return ユーザーID
     */
    public UUID getUserId() {
        return userId;
    }

    /**
     * 現在のページ数を取得します。
     *
     * @return 現在のページ数
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * 進捗更新日時を取得します。
     *
     * @return 更新日時
     */
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
