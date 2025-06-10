package com.readingshare.room.domain.repository;

import java.util.List;
import java.util.Optional;

import com.readingshare.room.domain.model.Room;

/**
 * Domain repository interface for Room aggregate.
 */
public interface RoomRepository {
    Optional<Room> findById(Long id);

    Optional<Room> findByName(String name);

    List<Room> findAll();

    boolean existsByName(String name);

    Room save(Room room);
}
