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

    @JsonIgnore
    @Column(nullable = true)
    private String roomPasswordHash;

    @Column(nullable = true)
    private Integer totalPages;      // 本のページ数

    @Column(nullable = true)
    private Integer pageTurnSpeed;   // ページめくり速度

    @Column(nullable = true, length = 100)
    private String genre;

    @Column(nullable = true)
    private Instant startTime;

    @Column(nullable = true)
    private Instant endTime;

    // --- コンストラクタ ---
    public Room() {
        // JPA用
    }

    public Room(String roomName, String bookTitle, UUID hostUserId, Integer totalPages, String genre, Instant startTime, Instant endTime, Integer pageTurnSpeed) {
        this.id = UUID.randomUUID();
        this.roomName = roomName;
        this.bookTitle = bookTitle;
        this.hostUserId = hostUserId;
        this.createdAt = Instant.now();
        this.totalPages = totalPages;
        this.genre = genre;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pageTurnSpeed = pageTurnSpeed;
    }

    public Room(UUID id, String roomName, String bookTitle, UUID hostUserId, Instant createdAt, Integer totalPages, String genre, Instant startTime, Instant endTime, Integer pageSpeed) {
        this.id = id;
        this.roomName = roomName;
        this.bookTitle = bookTitle;
        this.hostUserId = hostUserId;
        this.createdAt = createdAt;
        this.totalPages = totalPages;
        this.genre = genre;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // --- Getter / Setter ---
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

    public String getRoomPasswordHash() {
        return roomPasswordHash;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public String getGenre() {
        return genre;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public Integer getPageTurnSpeed() {
        return pageTurnSpeed;
    }

    /**
     * パスワードが設定されているかどうかを判定する。
     * JSONシリアライゼーション時にクライアントに返される。
     *
     * @return パスワードが設定されている場合はtrue、そうでなければfalse
     */
    public boolean isHasPassword() {
        return roomPasswordHash != null && !roomPasswordHash.isEmpty();
    }

    // --- セッター ---
    public void setRoomPasswordHash(String roomPasswordHash) {
        this.roomPasswordHash = roomPasswordHash;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public void setPageTurnSpeed(Integer pageTurnSpeed) {
        this.pageTurnSpeed = pageTurnSpeed;
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
                ", totalPages=" + totalPages +
                ", pageTurnSpeed=" + pageTurnSpeed +
                ", genre='" + genre + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
