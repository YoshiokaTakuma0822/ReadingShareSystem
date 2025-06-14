package com.readingshare.chat.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

/**
 * ユーザーの読書進捗エンティティ。
 */
@Entity
@Table(name = "user_progress")
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 進捗情報ID (主キー)

    @NotNull
    @Column(name = "room_id", nullable = false)
    private Long roomId; // 関連する部屋ID

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId; // 進捗を記録するユーザーID

    @NotNull
    @Min(0)
    @Column(name = "current_page", nullable = false)
    private int currentPage; // 現在のページ数

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt; // 進捗更新日時

    // デフォルトコンストラクタ
    public UserProgress() {}

    public UserProgress(Long id, Long roomId, Long userId, int currentPage, Instant updatedAt) {
        this.id = id;
        this.roomId = roomId;
        this.userId = userId;
        this.currentPage = currentPage;
        this.updatedAt = updatedAt;
    }

    // Getters
    public Long getId() { return id; }
    public Long getRoomId() { return roomId; }
    public Long getUserId() { return userId; }
    public int getCurrentPage() { return currentPage; }
    public Instant getUpdatedAt() { return updatedAt; }

    // Setters (JPAのためにidは必要)
    public void setId(Long id) { this.id = id; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}