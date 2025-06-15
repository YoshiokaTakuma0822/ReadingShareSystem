package com.readingshare.state.service;

import com.readingshare.state.domain.model.RoomReadingState;
import com.readingshare.state.domain.model.UserReadingState;
import com.readingshare.state.domain.repository.RoomReadingStateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoomReadingStateServiceTest {
    private RoomReadingStateRepository repository;
    private RoomReadingStateService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(RoomReadingStateRepository.class);
        service = new RoomReadingStateService(repository);
    }

    @Test
    void testTurnPageNext() {
        RoomReadingState room = new RoomReadingState("room1");
        UserReadingState user = new UserReadingState("user1", "ユーザー1", "/icon.png", 1, "", 60);
        room.updateUserState(user);
        when(repository.findByRoomId("room1")).thenReturn(room);

        UserReadingState updated = service.turnPage("room1", "user1", "next");
        assertNotNull(updated);
        assertEquals(2, updated.getCurrentPage());
        assertEquals(57, updated.getAutoTurnIntervalSec());
    }

    @Test
    void testTurnPagePrev() {
        RoomReadingState room = new RoomReadingState("room1");
        UserReadingState user = new UserReadingState("user1", "ユーザー1", "/icon.png", 2, "", 60);
        room.updateUserState(user);
        when(repository.findByRoomId("room1")).thenReturn(room);

        UserReadingState updated = service.turnPage("room1", "user1", "prev");
        assertNotNull(updated);
        assertEquals(1, updated.getCurrentPage());
        assertEquals(63, updated.getAutoTurnIntervalSec());
    }

    @Test
    void testAutoTurnPage() {
        RoomReadingState room = new RoomReadingState("room1");
        UserReadingState user = new UserReadingState("user1", "ユーザー1", "/icon.png", 3, "", 60);
        room.updateUserState(user);
        when(repository.findByRoomId("room1")).thenReturn(room);

        UserReadingState updated = service.autoTurnPage("room1", "user1");
        assertNotNull(updated);
        assertEquals(4, updated.getCurrentPage());
        assertEquals(60, updated.getAutoTurnIntervalSec());
    }
}
