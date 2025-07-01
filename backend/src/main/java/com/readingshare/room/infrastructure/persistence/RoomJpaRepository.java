package com.readingshare.room.infrastructure.persistence;

import java.time.Instant;
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
     * 複数条件で部屋を検索する。DBレベルでフィルタ
     * openOnly/closedOnlyは両方trueの場合はopenOnly優先、両方falseまたはnullなら全件
     */
    @Query("SELECT r FROM Room r WHERE "
         + "(COALESCE(:keyword, '') = '' OR r.roomName LIKE CONCAT('%', :keyword, '%') OR r.bookTitle LIKE CONCAT('%', :keyword, '%')) "
         + "AND (COALESCE(:genre, '') = '' OR r.genre = :genre) "
         + "AND (COALESCE(:startFrom, r.startTime) <= r.startTime OR r.startTime IS NULL) "
         + "AND (COALESCE(:startTo, r.startTime) >= r.startTime OR r.startTime IS NULL) "
         + "AND (COALESCE(:endFrom, r.endTime) <= r.endTime OR r.endTime IS NULL) "
         + "AND (COALESCE(:endTo, r.endTime) >= r.endTime OR r.endTime IS NULL) "
         + "AND (COALESCE(:pagesMin, r.totalPages) <= r.totalPages OR r.totalPages IS NULL) "
         + "AND (COALESCE(:pagesMax, r.totalPages) >= r.totalPages OR r.totalPages IS NULL) "
         + "AND ( (:openOnly = true AND (r.endTime IS NULL OR r.endTime > :now)) "
         + "   OR (:closedOnly = true AND r.endTime IS NOT NULL AND r.endTime <= :now) "
         + "   OR ((:openOnly IS NULL OR :openOnly = false) AND (:closedOnly IS NULL OR :closedOnly = false)) )")
    List<Room> findByConditions(
            @Param("keyword") String keyword,
            @Param("genre") String genre,
            @Param("startFrom") Instant startFrom,
            @Param("startTo") Instant startTo,
            @Param("endFrom") Instant endFrom,
            @Param("endTo") Instant endTo,
            @Param("pagesMin") Integer pagesMin,
            @Param("pagesMax") Integer pagesMax,
            @Param("openOnly") Boolean openOnly,
            @Param("closedOnly") Boolean closedOnly,
            @Param("now") Instant now
    );
}
