package com.example.readingshare.state.service;

import com.example.readingshare.state.domain.model.RoomReadingState;
import com.example.readingshare.state.domain.model.UserReadingState;
import com.example.readingshare.state.domain.repository.RoomReadingStateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoomReadingStateServiceTest {
    private RoomReadingStateRepository repository;
    private RoomReadingStateService service;

    @BeforeEach
    void setUp() {
        repository = mock(RoomReadingStateRepository.class);
        service = new RoomReadingStateService(repository);
    }

    @Test
    void updateUserReadingState_createsNewRoomIfNotExists() {
        String roomId = "room1";
        UserReadingState userState = new UserReadingState("user1", 5, "comment");
        when(repository.findByRoomId(roomId)).thenReturn(null);

        service.updateUserReadingState(roomId, userState);

        ArgumentCaptor<RoomReadingState> captor = ArgumentCaptor.forClass(RoomReadingState.class);
        verify(repository).save(captor.capture());
        RoomReadingState saved = captor.getValue();
        assertEquals(roomId, saved.getRoomId());
        assertEquals(userState, saved.getUserState("user1"));
    }

    @Test
    void updateUserReadingState_updatesExistingRoom() {
        String roomId = "room2";
        RoomReadingState existing = new RoomReadingState(roomId);
        UserReadingState userState = new UserReadingState("user2", 10, "test");
        when(repository.findByRoomId(roomId)).thenReturn(existing);

        service.updateUserReadingState(roomId, userState);

        verify(repository).save(existing);
        assertEquals(userState, existing.getUserState("user2"));
    }

    @Test
    void getRoomReadingState_returnsRoomState() {
        String roomId = "room3";
        RoomReadingState state = new RoomReadingState(roomId);
        when(repository.findByRoomId(roomId)).thenReturn(state);

        RoomReadingState result = service.getRoomReadingState(roomId);
        assertEquals(state, result);
    }
}
