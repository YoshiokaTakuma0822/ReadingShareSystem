package com.example.readingshare.state.infrastructure;

import com.example.readingshare.state.domain.model.RoomReadingState;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoomReadingStateRepositoryImplTest {
    @Test
    void testSaveAndFindByRoomId() {
        RoomReadingStateRepositoryImpl repo = new RoomReadingStateRepositoryImpl();
        RoomReadingState state = new RoomReadingState("room1");
        repo.save(state);
        assertEquals(state, repo.findByRoomId("room1"));
    }

    @Test
    void testFindByRoomIdReturnsNullIfNotFound() {
        RoomReadingStateRepositoryImpl repo = new RoomReadingStateRepositoryImpl();
        assertNull(repo.findByRoomId("notfound"));
    }
}
