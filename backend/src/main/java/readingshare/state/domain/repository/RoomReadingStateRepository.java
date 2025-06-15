package readingshare.state.domain.repository;

import readingshare.state.domain.model.RoomReadingState;

public interface RoomReadingStateRepository {
    RoomReadingState findByRoomId(String roomId);
    void save(RoomReadingState roomReadingState);
}
