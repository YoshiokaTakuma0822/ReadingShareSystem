package com.readingshare.auth.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

/**
 * F1 会員情報に基くユーザーエンティティ。
 */
@Entity
@Table(name = "users") // テーブル名は "users" が一般的
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // user id (主キー)

    @NotNull
    @Column(name = "username", unique = true, nullable = false)
    private String username; // ユーザの表示名

    @Column(name = "password_hash", nullable = false)
    private String passwordHash; // パスワードのハッシュ

    @Lob // Large Object for potentially large JSON content
    @Column(name = "contents", columnDefinition = "json") // contents (JSON形式で保存される想定)
    private String contents; // JSONとして保存される内容

    @NotNull
    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt; // アカウント作成時刻

    // デフォルトコンストラクタ (JPAのために必要)
    public User() {}

    public User(Long id, String username, String contents, Instant joinedAt) {
        this.id = id;
        this.username = username;
        this.contents = contents;
        this.joinedAt = joinedAt;
    }

    // パスワードハッシュを設定するためのコンストラクタ
    public User(String username, String passwordHash, String contents, Instant joinedAt) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.contents = contents;
        this.joinedAt = joinedAt;
    }

    // Getters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getContents() { return contents; }
    public Instant getJoinedAt() { return joinedAt; }

    // Setters (必要なもののみ、JPAで自動生成されるIDや登録日時などは設定しないことも多い)
    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setContents(String contents) { this.contents = contents; }
    public void setJoinedAt(Instant joinedAt) { this.joinedAt = joinedAt; }
}