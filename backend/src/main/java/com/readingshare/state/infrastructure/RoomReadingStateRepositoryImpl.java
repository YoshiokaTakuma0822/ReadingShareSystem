package com.readingshare.state.infrastructure;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.readingshare.state.domain.model.RoomReadingState;
import com.readingshare.state.domain.repository.RoomReadingStateRepository;

/**
 * RoomReadingStateRepositoryImpl は、部屋の読書状態を管理するリポジトリの実装クラスです。
 * データはスレッドセーフな ConcurrentHashMap を使用して管理されます。
 */
@Repository
public class RoomReadingStateRepositoryImpl implements RoomReadingStateRepository {
    private final Map<String, RoomReadingState> store = new ConcurrentHashMap<>();

    /**
     * 部屋IDを使用して部屋の読書状態を検索します。
     *
     * @param roomId 検索対象の部屋ID
     * @return 部屋の読書状態、存在しない場合は null
     */
    @Override
    public RoomReadingState findByRoomId(String roomId) {
        return store.get(roomId);
    }

    /**
     * 部屋の読書状態を保存します。
     *
     * @param roomReadingState 保存する部屋の読書状態
     */
    @Override
    public void save(RoomReadingState roomReadingState) {
        store.put(roomReadingState.getRoomId(), roomReadingState);
    }
}
