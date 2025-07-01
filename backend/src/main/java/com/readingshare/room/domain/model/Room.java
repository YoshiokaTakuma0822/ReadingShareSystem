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
    private Instant endTime; // 終了時刻

    // --- フィールド ---
    @Column(nullable = true)
    private Integer maxPage; // dev_al23060_merge_test: 最大ページ数

    @Column(nullable = false)
    private int totalPages; // 本のページ数

    private transient boolean hasPassword; // DBには保存しない、APIレスポンス用

    @JsonIgnore
    @Column(name = "password_hash")
    private String passwordHash; // パスワードハッシュ（null可）

    @Column(nullable = true, length = 100)
    private String genre; // 部屋のジャンル

    @Column(nullable = true)
    private Instant startTime; // 開始時刻

    @Column(nullable = true)
    private Integer pageSpeed; // dev_al23060_merge_test: ページめくり速度

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

    public Room(String roomName, String bookTitle, UUID hostUserId, int totalPages) {
        this.id = UUID.randomUUID();
        this.roomName = roomName;
        this.bookTitle = bookTitle;
        this.hostUserId = hostUserId;
        this.createdAt = Instant.now();
        this.totalPages = totalPages;
    }

    public Room(UUID id, String roomName, String bookTitle, UUID hostUserId, Instant createdAt, Instant endTime) {
        this.id = id;
        this.roomName = roomName;
        this.bookTitle = bookTitle;
        this.hostUserId = hostUserId;
        this.createdAt = createdAt;
        this.endTime = endTime;
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

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(Integer maxPage) {
        this.maxPage = maxPage;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Integer getPageSpeed() {
        return pageSpeed;
    }

    public void setPageSpeed(Integer pageSpeed) {
        this.pageSpeed = pageSpeed;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    // --- パスワード有無はpasswordHashの有無で判定する ---
    public boolean isHasPassword() {
        return passwordHash != null && !passwordHash.isEmpty();
    }

    public void setHasPassword(boolean hasPassword) {
        this.hasPassword = hasPassword;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setHostUserId(UUID hostUserId) {
        this.hostUserId = hostUserId;
    }

    public UUID getId() {
        return id;
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
