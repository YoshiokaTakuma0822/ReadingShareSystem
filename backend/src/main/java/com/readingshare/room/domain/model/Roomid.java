package com.readingshare.room.domain.model;

import java.util.Objects;

/**
 * 値オブジェクト: RoomId
 * Room の識別子 (room_id) をラップする Value Object。
 */
public class Roomid {

    private final Long value;

    public Roomid(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("RoomId value must not be null");
        }
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Roomid)) return false;
        Roomid roomId = (Roomid) o;
        return Objects.equals(value, roomId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "RoomId{" +
                "value=" + value +
                '}';
    }
}
