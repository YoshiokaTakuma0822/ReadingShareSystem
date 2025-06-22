package com.readingshare.room.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.readingshare.room.domain.model.Room;

/**
 * 部屋情報のJPAリポジトリインターフェース。
 * 担当: 芳岡
 */
@Repository
public interface RoomJpaRepository extends JpaRepository<Room, UUID> {

    /**
     * 指定されたキーワードに一致する部屋を検索する。
     * 部屋名または本タイトルにキーワードが含まれる部屋を返す。
     *
     * @param keyword 検索キーワード
     * @return 検索結果の部屋リスト
     */
    @Query("SELECT r FROM Room r WHERE r.roomName LIKE %:keyword% OR r.bookTitle LIKE %:keyword%")
    List<Room> findByKeyword(@Param("keyword") String keyword);

    /**
     * 指定されたジャンルに一致する部屋を検索する。
     *
     * @param genre 検索ジャンル
     * @return 検索結果の部屋リスト
     */
    @Query("SELECT r FROM Room r WHERE r.genre = :genre")
    List<Room> findByGenre(@Param("genre") String genre);
}
