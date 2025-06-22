package com.readingshare.state.domain.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomReadingState {
    private final String roomId;
    private final Map<String, UserReadingState> userStates;

    public RoomReadingState(String roomId) {
        this.roomId = roomId;
        this.userStates = new HashMap<>();
    }

    public String getRoomId() {
        return roomId;
    }

    public Map<String, UserReadingState> getUserStates() {
        return userStates;
    }

    public void updateUserState(UserReadingState state) {
        userStates.put(state.getUserId(), state);
    }

    public UserReadingState getUserState(String userId) {
        return userStates.get(userId);
    }

    public List<UserReadingState> getAllUserStates() {
        return List.copyOf(userStates.values());
    }
}
