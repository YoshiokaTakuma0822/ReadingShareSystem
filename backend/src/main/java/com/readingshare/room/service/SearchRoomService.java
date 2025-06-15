package com.readingshare.room.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.common.exception.DatabaseAccessException;
import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.repository.IRoomRepository;

/**
 * 部屋検索のアプリケーションサービス。
 * 担当: 芳岡
 */
@Service
public class SearchRoomService {

    private final IRoomRepository roomRepository;

    public SearchRoomService(IRoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    /**
     * キーワードに基づいて部屋を検索する。
     *
     * @param keyword 検索キーワード（nullまたは空文字列の場合は全件検索）
     * @return 検索結果の部屋リスト
     * @throws DatabaseAccessException データベースアクセスエラー時
     */
    @Transactional(readOnly = true)
    public List<Room> searchRooms(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return roomRepository.findAll();
        } else {
            return roomRepository.findByKeyword(keyword);
        }
    }

    /**
     * 部屋IDに基づいて部屋を検索する。
     *
     * @param roomId 部屋ID
     * @return 部屋が見つかった場合はOptionalにRoom、見つからない場合はOptional.empty()
     * @throws DatabaseAccessException データベースアクセスエラー時
     */
    @Transactional(readOnly = true)
    public Optional<Room> getRoomById(UUID roomId) {
        return roomRepository.findById(roomId);
    }
}
