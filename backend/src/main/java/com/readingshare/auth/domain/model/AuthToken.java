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
 *
 * @author 003
 * @componentIdName C02 ログイン・会員登録
 * @moduleIdName M0203 認証トークンエンティティ
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

    /**
     * デフォルトコンストラクタ。
     * JPAの要件により必要です。
     */
    public AuthToken() {
    }

    /**
     * トークン値、ユーザー、作成時刻、有効期限を指定して認証トークンを作成します。
     *
     * @param tokenValue トークン値
     * @param user       トークンの所有者
     * @param createdAt  トークン作成時刻
     * @param expiresAt  トークン有効期限
     */
    public AuthToken(String tokenValue, User user, Instant createdAt, Instant expiresAt) {
        this.id = UUID.randomUUID();
        this.tokenValue = tokenValue;
        this.user = user;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.isActive = true;
    }

    /**
     * トークンIDを取得します。
     *
     * @return トークンID
     */
    public UUID getId() {
        return id;
    }

    /**
     * トークン値を取得します。
     *
     * @return トークン値
     */
    public String getTokenValue() {
        return tokenValue;
    }

    /**
     * トークンの所有者を取得します。
     *
     * @return トークンの所有者
     */
    public User getUser() {
        return user;
    }

    /**
     * トークン作成時刻を取得します。
     *
     * @return トークン作成時刻
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * トークン有効期限を取得します。
     *
     * @return トークン有効期限
     */
    public Instant getExpiresAt() {
        return expiresAt;
    }

    /**
     * トークンの最終使用時刻を取得します。
     *
     * @return トークンの最終使用時刻
     */
    public Instant getLastUsedAt() {
        return lastUsedAt;
    }

    /**
     * トークンが有効かどうかを取得します。
     *
     * @return トークンが有効である場合はtrue、それ以外はfalse
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * トークンの最終使用時刻を設定します。
     *
     * @param lastUsedAt トークンの最終使用時刻
     */
    public void setLastUsedAt(Instant lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    /**
     * トークンを無効化します。
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * トークンが期限切れかどうかを判定します。
     *
     * @return 期限切れの場合はtrue、それ以外はfalse
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    /**
     * トークンが有効かつ期限切れでないかどうかを判定します。
     *
     * @return 有効かつ期限切れでない場合はtrue、それ以外はfalse
     */
    public boolean isValid() {
        return isActive && !isExpired();
    }
}
