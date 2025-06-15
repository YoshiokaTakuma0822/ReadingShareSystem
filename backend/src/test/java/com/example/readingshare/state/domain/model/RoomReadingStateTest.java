package com.example.readingshare.state.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoomReadingStateTest {
    @Test
    void testUpdateAndGetUserState() {
        RoomReadingState room = new RoomReadingState("room1");
        UserReadingState user = new UserReadingState("user1", 1, "comment");
        room.updateUserState(user);
        assertEquals(user, room.getUserState("user1"));
    }

    @Test
    void testGetAllUserStates() {
        RoomReadingState room = new RoomReadingState("room2");
        UserReadingState user1 = new UserReadingState("user1", 2, "a");
        UserReadingState user2 = new UserReadingState("user2", 3, "b");
        room.updateUserState(user1);
        room.updateUserState(user2);
        assertTrue(room.getAllUserStates().contains(user1));
        assertTrue(room.getAllUserStates().contains(user2));
        assertEquals(2, room.getAllUserStates().size());
    }
}
