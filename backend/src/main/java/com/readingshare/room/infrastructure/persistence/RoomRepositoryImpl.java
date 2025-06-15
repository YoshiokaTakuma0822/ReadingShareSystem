package com.readingshare.room.infrastructure.persistence;

import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.model.RoomId;
import com.readingshare.room.domain.repository.IRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RoomRepositoryImpl implements IRoomRepository {
    @Autowired
    @Lazy
    private RoomRepository roomRepository;

    @Override
    public Room save(Room room) {
        return roomRepository.save(room);
    }

    @Override
    public Optional<Room> findById(RoomId id) {
        return roomRepository.findById(id);
    }

    @Override
    public List<Room> findByKeyword(String keyword) {
        return roomRepository.findByKeyword(keyword);
    }

    @Override
    public List<Room> findAll() {
        return roomRepository.findAll();
    }
}
