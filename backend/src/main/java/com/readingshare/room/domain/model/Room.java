package com.readingshare.room.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(nullable = false, length = 100)
    private String roomName;

    @Column(nullable = false, length = 200)
    private String bookTitle;

    @Column(nullable = false, columnDefinition = "UUID")
    private UUID hostUserId;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = true)
    private Instant endTime;

    @Column(nullable = false)
    private int totalPages; // 追加: 本のページ数

    private transient boolean hasPassword; // DBには保存しない、APIレスポンス用

    @JsonIgnore
    @Column(name = "password_hash")
    private String passwordHash; // パスワードハッシュ（null可）

    @Column(nullable = true)
    private Integer maxPage;

    @Column(nullable = true, length = 100)
    private String genre;

    @Column(nullable = true)
    private Instant startTime;

    @Column(nullable = true)
    private Integer pageSpeed;

    // --- コンストラクタ ---
    public Room() {
        // JPA用
    }

    public Room(String roomName, String bookTitle, UUID hostUserId, Integer maxPage, String genre, Instant startTime, Instant endTime, Integer pageSpeed) {
        this.id = UUID.randomUUID();
        this.roomName = roomName;
        this.bookTitle = bookTitle;
        this.hostUserId = hostUserId;
        this.createdAt = Instant.now();
        this.maxPage = maxPage;
        this.genre = genre;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pageSpeed = pageSpeed;
    }

    public Room(UUID id, String roomName, String bookTitle, UUID hostUserId, Instant createdAt, Integer maxPage, String genre, Instant startTime, Instant endTime, Integer pageSpeed) {
        this.id = id;
        this.roomName = roomName;
        this.bookTitle = bookTitle;
        this.hostUserId = hostUserId;
        this.createdAt = createdAt;
        this.maxPage = maxPage;
        this.genre = genre;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pageSpeed = pageSpeed;
    }

    // --- ゲッター ---
    public UUID getId() {
        return id;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public UUID getHostUserId() {
        return hostUserId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getMaxPage() {
        return maxPage;
    }

    public String getGenre() {
        return genre;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Integer getPageSpeed() {
        return pageSpeed;
    }

    /**
     * パスワードが設定されているかどうかを判定する。
     * JSONシリアライゼーション時にクライアントに返される。
     *
     * @return パスワードが設定されている場合はtrue、そうでなければfalse
     */
    public boolean isHasPassword() {
        return passwordHash != null && !passwordHash.isEmpty();
    }

    // setHasPasswordはAPIレスポンス用に残す場合はOK（不要なら削除可）
    public void setHasPassword(boolean hasPassword) {
        this.hasPassword = hasPassword;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setMaxPage(Integer maxPage) {
        this.maxPage = maxPage;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public void setPageSpeed(Integer pageSpeed) {
        this.pageSpeed = pageSpeed;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setHostUserId(UUID hostUserId) {
        this.hostUserId = hostUserId;
    }

    // --- その他のメソッド ---
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Room room = (Room) o;
        return Objects.equals(id, room.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", roomName='" + roomName + '\'' +
                ", bookTitle='" + bookTitle + '\'' +
                ", hostUserId=" + hostUserId +
                ", createdAt=" + createdAt +
                ", endTime=" + endTime +
                ", totalPages=" + totalPages +
                '}';
    }
}
