package com.readingshare.room.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * F2 部屋情報に基く部屋エンティティ。
 */
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 部屋ID (主キー)

    @NotNull
    @Column(name = "room_name", nullable = false)
    private String roomName; // 部屋名

    @NotNull
    @Column(name = "book_title", nullable = false)
    private String bookTitle; // 読んでいる本のタイトル

    @NotNull
    @Column(name = "host_user_id", nullable = false)
    private Long hostUserId; // ホストユーザーのID (F1 会員情報と連携)

    @Column(name = "room_password_hash") // オプションのパスワードハッシュ
    private String roomPasswordHash;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt; // 部屋作成日時

    @OneToMany(mappedBy = "roomId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RoomMember> members = new HashSet<>(); // 部屋のメンバー

    // デフォルトコンストラクタ (JPAのために必要)
    public Room() {}

    public Room(Long id, String roomName, String bookTitle, Long hostUserId, Instant createdAt) {
        this.id = id;
        this.roomName = roomName;
        this.bookTitle = bookTitle;
        this.hostUserId = hostUserId;
        this.createdAt = createdAt;
    }

    // Getters
    public Long getId() { return id; }
    public String getRoomName() { return roomName; }
    public String getBookTitle() { return bookTitle; }
    public Long getHostUserId() { return hostUserId; }
    public String getRoomPasswordHash() { return roomPasswordHash; }
    public Instant getCreatedAt() { return createdAt; }
    public Set<RoomMember> getMembers() { return members; }

    // Setters (JPAのためにidは必要, パスワードハッシュはドメインサービスで設定される)
    public void setId(Long id) { this.id = id; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public void setHostUserId(Long hostUserId) { this.hostUserId = hostUserId; }
    public void setRoomPasswordHash(String roomPasswordHash) { this.roomPasswordHash = roomPasswordHash; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setMembers(Set<RoomMember> members) { this.members = members; }

    /**
     * メンバーを追加するヘルパーメソッド。
     * RoomMember側の関連付けも同時に設定する。
     * @param member 追加するRoomMemberエンティティ
     */
    public void addMember(RoomMember member) {
        this.members.add(member);
        // member.setRoomId(this.id); // RoomMember側で設定
    }

    /**
     * パスワードが設定されているかチェックする。
     * @return パスワードが設定されていればtrue
     */
    public boolean hasPassword() {
        return this.roomPasswordHash != null && !this.roomPasswordHash.isEmpty();
    }
}