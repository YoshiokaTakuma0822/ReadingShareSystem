package readingshare.state.infrastructure;

import readingshare.state.domain.model.RoomReadingState;
import readingshare.state.domain.repository.RoomReadingStateRepository;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

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
