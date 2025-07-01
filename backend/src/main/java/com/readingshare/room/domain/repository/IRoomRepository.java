package com.readingshare.room.domain.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.readingshare.room.domain.model.Room;

/**
 * 部屋情報の永続化を担当するリポジトリインターフェース。
 * 担当: 芳岡
 */
public interface IRoomRepository {

    /**
     * 部屋を保存する。
     *
     * @param room 保存する部屋エンティティ
     * @return 保存された部屋エンティティ
     */
    Room save(Room room);

    /**
     * 部屋IDで部屋を検索する。
     *
     * @param id 部屋ID
     * @return 部屋が見つかった場合はOptionalにRoom、見つからない場合はOptional.empty()
     */
    Optional<Room> findById(UUID id);

    /**
     * 指定されたキーワードに一致する部屋を検索する。
     * 部屋名または本タイトルにキーワードが含まれる部屋を返す。
     *
     * @param keyword 検索キーワード
     * @return 検索結果の部屋リスト
     */
    List<Room> findByKeyword(String keyword);

    /**
     * 全ての部屋を取得する。
     *
     * @return 全ての部屋のリスト
     */
    List<Room> findAll();

    /**
     * ページングして部屋一覧を取得する。
     *
     * @param pageable ページング情報
     * @return ページングされた部屋のリスト
     */
    Page<Room> findAll(Pageable pageable);

    /**
     * 部屋IDで部屋を削除する。
     * @param id 部屋ID
     */
    void deleteById(UUID id);

    /**
     * 複数条件で部屋を検索する。
     */
    List<Room> findByConditions(
            String keyword,
            String genre,
            Instant startFrom,
            Instant startTo,
            Instant endFrom,
            Instant endTo,
            Integer pagesMin,
            Integer pagesMax,
            Boolean openOnly,
            Boolean closedOnly,
            Instant now
    );
}
