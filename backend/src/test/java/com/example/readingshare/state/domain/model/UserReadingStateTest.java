package com.example.readingshare.state.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserReadingStateTest {
    @Test
    void testGetters() {
        UserReadingState state = new UserReadingState("user1", 10, "test");
        assertEquals("user1", state.getUserId());
        assertEquals(10, state.getCurrentPage());
        assertEquals("test", state.getComment());
    }

    @Test
    void testEqualsAndHashCode() {
        UserReadingState state1 = new UserReadingState("user1", 1, "a");
        UserReadingState state2 = new UserReadingState("user1", 2, "b");
        UserReadingState state3 = new UserReadingState("user2", 1, "a");
        assertEquals(state1, state2);
        assertNotEquals(state1, state3);
        assertEquals(state1.hashCode(), state2.hashCode());
    }
}
