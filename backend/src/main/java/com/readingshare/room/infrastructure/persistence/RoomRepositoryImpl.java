package com.readingshare.room.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.repository.IRoomRepository;

@Repository
public class RoomRepositoryImpl implements IRoomRepository {
    @Autowired
    @Lazy
    private RoomJpaRepository roomRepository;

    @Override
    public Room save(Room room) {
        return roomRepository.save(room);
    }

    @Override
    public Optional<Room> findById(UUID id) {
        return roomRepository.findById(id);
    }

    @Override
    public List<Room> findByKeyword(String keyword) {
        return roomRepository.findByKeyword(keyword);
    }

    @Override
    public List<Room> findByGenre(String genre) {
        return roomRepository.findByGenre(genre);
    }

    @Override
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    @Override
    public Page<Room> findAll(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }

    @Override
    public void deleteById(UUID id) {
        roomRepository.deleteById(id);
    }

    @Override
    public <S extends Room> boolean exists(Example<S> example) {
        return roomRepository.exists(example);
    }

    @Override
    public Room getById(UUID id) {
        return roomRepository.getById(id);
    }
}
