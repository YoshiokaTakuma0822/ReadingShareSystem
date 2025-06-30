package com.readingshare.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * F1 会員情報に基くユーザーエンティティ。
 */
@Entity
@Table(name = "users") // テーブル名は "users" が一般的
public class User {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id; // user id (主キー)

    @NotNull
    @Column(name = "username", unique = true, nullable = false)
    private String username; // ユーザの表示名

    @Column(name = "password_hash", nullable = false)
    private String passwordHash; // パスワードのハッシュ

    @NotNull
    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt; // アカウント作成時刻

    @Column(name = "history_reset_at")
    private Instant historyResetAt;

    // デフォルトコンストラクタ (JPAのために必要)
    public User() {
    }

    public User(UUID id, String username, Instant joinedAt) {
        this.id = id;
        this.username = username;
        this.joinedAt = joinedAt;
    }

    // パスワードハッシュを設定するためのコンストラクタ
    public User(String username, String passwordHash, Instant joinedAt) {
        this.id = UUID.randomUUID(); // 新規作成時はUUIDを自動生成
        this.username = username;
        this.passwordHash = passwordHash;
        this.joinedAt = joinedAt;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public Instant getHistoryResetAt() {
        return historyResetAt;
    }

    // Setters (必要なもののみ)
    public void setId(UUID id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }

    public void setHistoryResetAt(Instant historyResetAt) {
        this.historyResetAt = historyResetAt;
    }
}
