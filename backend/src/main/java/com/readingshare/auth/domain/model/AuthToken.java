package com.readingshare.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * 認証トークンエンティティ。
 * Bearer Token認証のためのopaqueトークンを管理する。
 */
@Entity
@Table(name = "auth_tokens")
public class AuthToken {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @NotNull
    @Column(name = "token_value", unique = true, nullable = false, length = 128)
    private String tokenValue; // opaqueトークン値

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // トークンの所有者

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt; // トークン作成時刻

    @NotNull
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt; // トークン有効期限

    @Column(name = "last_used_at")
    private Instant lastUsedAt; // 最終使用時刻

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true; // トークンが有効かどうか

    // デフォルトコンストラクタ (JPAのために必要)
    public AuthToken() {
    }

    public AuthToken(String tokenValue, User user, Instant createdAt, Instant expiresAt) {
        this.id = UUID.randomUUID();
        this.tokenValue = tokenValue;
        this.user = user;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.isActive = true;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public User getUser() {
        return user;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getLastUsedAt() {
        return lastUsedAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    // Setters
    public void setLastUsedAt(Instant lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    public void deactivate() {
        this.isActive = false;
    }

    // ビジネスロジック
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return isActive && !isExpired();
    }
}
