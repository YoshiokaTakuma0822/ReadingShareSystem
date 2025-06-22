package com.readingshare.state.domain.repository;

import com.readingshare.state.domain.model.RoomReadingState;

/**
 * RoomReadingStateRepository は、部屋の読書状態を管理するためのリポジトリインターフェースです。
 * 部屋IDを使用して読書状態を検索したり、保存したりする機能を提供します。
 */
public interface RoomReadingStateRepository {
    /**
     * 部屋IDを使用して部屋の読書状態を検索します。
     *
     * @param roomId 検索対象の部屋ID
     * @return 部屋の読書状態、存在しない場合は null
     */
    RoomReadingState findByRoomId(String roomId);

    /**
     * 部屋の読書状態を保存します。
     *
     * @param roomReadingState 保存する部屋の読書状態
     */
    void save(RoomReadingState roomReadingState);
}
