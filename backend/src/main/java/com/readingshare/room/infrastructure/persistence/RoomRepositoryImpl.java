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

    @Override
    public <S extends Room> List<S> saveAll(Iterable<S> entities) {
        return roomRepository.saveAll(entities);
    }

    @Override
    public void delete(Room entity) {
        roomRepository.delete(entity);
    }

    @Override
    public <S extends Room> S saveAndFlush(S entity) {
        return roomRepository.saveAndFlush(entity);
    }

    @Override
    public void deleteAll(Iterable<? extends Room> entities) {
        roomRepository.deleteAll(entities);
    }

    @Override
    public void deleteAll() {
        roomRepository.deleteAll();
    }

    @Override
    public void flush() {
        roomRepository.flush();
    }

    @Override
    public Optional<Room> findByRoomName(String roomName) {
        return roomRepository.findByRoomName(roomName);
    }

    @Override
    public Room getOne(UUID id) {
        return roomRepository.getOne(id);
    }

    @Override
    public <S extends Room, R> R findBy(Example<S> example, java.util.function.Function<org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return roomRepository.findBy(example, queryFunction);
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UUID> ids) {
        roomRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public void deleteAllInBatch(Iterable<Room> entities) {
        roomRepository.deleteAllInBatch(entities);
    }

    @Override
    public void deleteAllInBatch() {
        roomRepository.deleteAllInBatch();
    }

    @Override
    public <S extends Room> List<S> findAll(Example<S> example, org.springframework.data.domain.Sort sort) {
        return roomRepository.findAll(example, sort);
    }

    @Override
    public <S extends Room> List<S> findAll(Example<S> example) {
        return roomRepository.findAll(example);
    }

    @Override
    public <S extends Room> Page<S> findAll(Example<S> example, Pageable pageable) {
        return roomRepository.findAll(example, pageable);
    }

    @Override
    public List<Room> findAll(org.springframework.data.domain.Sort sort) {
        return roomRepository.findAll(sort);
    }

    @Override
    public boolean existsById(UUID id) {
        return roomRepository.existsById(id);
    }

    @Override
    public <S extends Room> long count(Example<S> example) {
        return roomRepository.count(example);
    }

    @Override
    public long count() {
        return roomRepository.count();
    }

    @Override
    public <S extends Room> Optional<S> findOne(Example<S> example) {
        return roomRepository.findOne(example);
    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> ids) {
        roomRepository.deleteAllById(ids);
    }

    @Override
    public List<Room> findAllById(Iterable<UUID> ids) {
        return roomRepository.findAllById(ids);
    }

    @Override
    public <S extends Room> List<S> saveAllAndFlush(Iterable<S> entities) {
        return roomRepository.saveAllAndFlush(entities);
    }

    @Override
    public Room getReferenceById(UUID id) {
        return roomRepository.getReferenceById(id);
    }
}
