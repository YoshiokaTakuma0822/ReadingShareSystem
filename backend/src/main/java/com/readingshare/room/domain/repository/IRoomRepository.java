package com.readingshare.room.domain.repository;

import com.readingshare.room.domain.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface IRoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomName(String roomName);
    List<Room> findByRoomNameContaining(String keyword);
    List<Room> findRecentRooms(int limit);
}