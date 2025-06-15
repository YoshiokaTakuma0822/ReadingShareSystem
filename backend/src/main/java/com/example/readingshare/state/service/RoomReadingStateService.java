package com.example.readingshare.state.service;

import com.example.readingshare.state.domain.model.RoomReadingState;
import com.example.readingshare.state.domain.model.UserReadingState;
import com.example.readingshare.state.domain.repository.RoomReadingStateRepository;
import org.springframework.stereotype.Service;

@Service
public class RoomReadingStateService {
    private final RoomReadingStateRepository repository;

    public RoomReadingStateService(RoomReadingStateRepository repository) {
        this.repository = repository;
    }

    public void updateUserReadingState(String roomId, UserReadingState userState) {
        RoomReadingState roomState = repository.findByRoomId(roomId);
        if (roomState == null) {
            roomState = new RoomReadingState(roomId);
        }
        roomState.updateUserState(userState);
        repository.save(roomState);
    }

    public RoomReadingState getRoomReadingState(String roomId) {
        return repository.findByRoomId(roomId);
    }
}
