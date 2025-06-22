package com.readingshare.state.infrastructure;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.readingshare.state.domain.model.RoomReadingState;
import com.readingshare.state.domain.repository.RoomReadingStateRepository;

@Repository
public class RoomReadingStateRepositoryImpl implements RoomReadingStateRepository {
    private final Map<String, RoomReadingState> store = new ConcurrentHashMap<>();

    @Override
    public RoomReadingState findByRoomId(String roomId) {
        return store.get(roomId);
    }

    @Override
    public void save(RoomReadingState roomReadingState) {
        store.put(roomReadingState.getRoomId(), roomReadingState);
    }
}
