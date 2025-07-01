package com.readingshare.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * 会員情報に基くユーザーエンティティ。
 *
 * @author 02005
 * @componentId C2
 * @moduleName ユーザーエンティティ
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

    /**
     * デフォルトコンストラクタ。
     * JPAの要件により必要です。
     */
    public User() {
    }

    /**
     * ID、ユーザー名、作成日時を指定してユーザーを作成します。
     *
     * @param id       ユーザーID
     * @param username ユーザー名
     * @param joinedAt アカウント作成時刻
     */
    public User(UUID id, String username, Instant joinedAt) {
        this.id = id;
        this.username = username;
        this.joinedAt = joinedAt;
    }

    /**
     * ユーザー名、パスワードハッシュ、作成日時を指定してユーザーを作成します。
     *
     * @param username     ユーザー名
     * @param passwordHash パスワードのハッシュ
     * @param joinedAt     アカウント作成時刻
     */
    public User(String username, String passwordHash, Instant joinedAt) {
        this.id = UUID.randomUUID(); // 新規作成時はUUIDを自動生成
        this.username = username;
        this.passwordHash = passwordHash;
        this.joinedAt = joinedAt;
    }

    /**
     * ユーザーIDを取得します。
     *
     * @return ユーザーID
     */
    public UUID getId() {
        return id;
    }

    /**
     * ユーザー名を取得します。
     *
     * @return ユーザー名
     */
    public String getUsername() {
        return username;
    }

    /**
     * パスワードハッシュを取得します。
     *
     * @return パスワードハッシュ
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * アカウント作成時刻を取得します。
     *
     * @return アカウント作成時刻
     */
    public Instant getJoinedAt() {
        return joinedAt;
    }

    /**
     * ユーザーIDを設定します。
     *
     * @param id ユーザーID
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * ユーザー名を設定します。
     *
     * @param username ユーザー名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * パスワードハッシュを設定します。
     *
     * @param passwordHash パスワードハッシュ
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * アカウント作成時刻を設定します。
     *
     * @param joinedAt アカウント作成時刻
     */
    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }
}
