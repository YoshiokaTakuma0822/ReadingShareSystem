package com.readingshare.room.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Roomエンティティクラス。部屋情報を管理し、本の共有セッション用の情報を保持する。
 * 一意な識別子、名称、本タイトル、ホストユーザーID、作成日時、およびパスワード設定状態を含む。
 *
 * @author 02004
 * @componentId C3
 * @moduleName Roomドメインモデル
 */
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id; // 部屋ID

    @Column(nullable = false, length = 100)
    private String roomName; // 部屋名

    @Column(nullable = false, length = 200)
    private String bookTitle; // 本のタイトル

    @Column(nullable = false, columnDefinition = "UUID")
    private UUID hostUserId; // ホストユーザーID

    @Column(nullable = false)
    private Instant createdAt; // 作成日時

    @JsonIgnore
    @Column(nullable = true)
    private String roomPasswordHash; // 部屋のパスワードハッシュ

    /**
     * デフォルトコンストラクタ。
     * JPAの要件により必要です。
     */
    public Room() {
        // JPA用
    }

    public Room(String roomName, String bookTitle, UUID hostUserId) {
        this.id = UUID.randomUUID();
        this.roomName = roomName;
        this.bookTitle = bookTitle;
        this.hostUserId = hostUserId;
        this.createdAt = Instant.now();
    }

    public Room(UUID id, String roomName, String bookTitle, UUID hostUserId, Instant createdAt) {
        this.id = id;
        this.roomName = roomName;
        this.bookTitle = bookTitle;
        this.hostUserId = hostUserId;
        this.createdAt = createdAt;
    }

    // --- ゲッター ---
    /**
     * 部屋IDを取得します。
     *
     * @return 部屋ID
     */
    public UUID getId() {
        return id;
    }

    /**
     * 部屋名を取得します。
     *
     * @return 部屋名
     */
    public String getRoomName() {
        return roomName;
    }

    /**
     * 本のタイトルを取得します。
     *
     * @return 本のタイトル
     */
    public String getBookTitle() {
        return bookTitle;
    }

    /**
     * ホストユーザーIDを取得します。
     *
     * @return ホストユーザーID
     */
    public UUID getHostUserId() {
        return hostUserId;
    }

    /**
     * 作成日時を取得します。
     *
     * @return 作成日時
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * 部屋のパスワードハッシュを取得します。
     *
     * @return パスワードハッシュ
     */
    public String getRoomPasswordHash() {
        return roomPasswordHash;
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
    /**
     * 部屋のパスワードハッシュを設定します。
     *
     * @param roomPasswordHash パスワードハッシュ
     */
    public void setRoomPasswordHash(String roomPasswordHash) {
        this.roomPasswordHash = roomPasswordHash;
    }

    // --- その他のメソッド ---
    /**
     * このオブジェクトが他のオブジェクトと等しいかどうかを判定します。
     * 部屋IDが同じ場合に等しいとみなします。
     *
     * @param o 比較対象のオブジェクト
     * @return 等しい場合はtrue、そうでない場合はfalse
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Room room = (Room) o;
        return Objects.equals(id, room.id);
    }

    /**
     * このオブジェクトのハッシュコードを返します。
     * 部屋IDに基づいて計算されます。
     *
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * このオブジェクトの文字列表現を返します。
     *
     * @return 文字列表現
     */
    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", roomName='" + roomName + '\'' +
                ", bookTitle='" + bookTitle + '\'' +
                ", hostUserId=" + hostUserId +
                ", createdAt=" + createdAt +
                '}';
    }
}
