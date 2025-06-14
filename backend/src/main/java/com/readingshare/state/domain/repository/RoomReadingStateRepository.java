package com.readingshare.state.domain.repository;

import com.readingshare.state.domain.model.RoomReadingState;

public interface RoomReadingStateRepository {
    RoomReadingState findByRoomId(String roomId);
    void save(RoomReadingState roomReadingState);
}
