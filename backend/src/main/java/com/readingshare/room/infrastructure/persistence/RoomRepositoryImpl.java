package com.readingshare.room.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.repository.IRoomRepository;

/**
 * ルームリポジトリの実装クラス。データベース操作を担当します。
 *
 * @author 02004
 * @componentId C6
 * @moduleName ルームリポジトリ実装
 * @see RoomJpaRepository
 */
@Repository
public class RoomRepositoryImpl implements IRoomRepository {

    @Autowired
    private RoomJpaRepository roomRepository;

    /**
     * 部屋を保存します。
     *
     * @param room 保存する部屋
     * @return 保存された部屋
     */
    @Override
    public Room save(Room room) {
        return roomRepository.save(room);
    }

    /**
     * 指定されたIDの部屋を取得します。
     *
     * @param id 部屋のID
     * @return 指定されたIDの部屋を含むOptional、見つからない場合は空のOptional
     */
    @Override
    public Optional<Room> findById(UUID id) {
        return roomRepository.findById(id);
    }

    /**
     * 指定されたキーワードに一致する部屋を検索します。
     *
     * @param keyword 検索キーワード
     * @return キーワードに一致する部屋のリスト
     */
    @Override
    public List<Room> findByKeyword(String keyword) {
        return roomRepository.findByKeyword(keyword);
    }

    /**
     * すべての部屋を取得します。
     *
     * @return すべての部屋のリスト
     */
    @Override
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    /**
     * ページング情報を用いて部屋のページ一覧を取得します。
     *
     * @param pageable ページングおよびソート情報
     * @return ページ化された部屋リスト
     */
    @Override
    public Page<Room> findAll(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }
}
