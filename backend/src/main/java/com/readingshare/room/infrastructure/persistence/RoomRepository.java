package com.readingshare.room.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.model.RoomId;

/**
 * 部屋情報のJPAリポジトリインターフェース。
 * 担当: 芳岡
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    /**
     * 部屋IDで部屋を検索する。
     *
     * @param id 部屋ID
     * @return 部屋が見つかった場合はOptionalにRoom、見つからない場合はOptional.empty()
     */
    Optional<Room> findById(RoomId id);

    /**
     * 指定されたキーワードに一致する部屋を検索する。
     * 部屋名または本タイトルにキーワードが含まれる部屋を返す。
     *
     * @param keyword 検索キーワード
     * @return 検索結果の部屋リスト
     */
    @Query("SELECT r FROM Room r WHERE r.roomName LIKE %:keyword% OR r.bookTitle LIKE %:keyword%")
    List<Room> findByKeyword(@Param("keyword") String keyword);
}
