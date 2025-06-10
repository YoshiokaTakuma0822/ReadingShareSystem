package com.readingshare.room.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.account.infrastructure.persistence.AccountEntity;
import com.readingshare.account.infrastructure.repository.AccountJpaRepository;
import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.repository.RoomRepository;
import com.readingshare.room.infrastructure.persistence.RoomEntity;

/**
 * Adapter implementing domain RoomRepository via JPA.
 */
@Repository
@Transactional
public class RoomRepositoryImpl implements RoomRepository {
    private final RoomJpaRepository roomJpaRepository;
    private final AccountJpaRepository accountJpaRepository;

    public RoomRepositoryImpl(RoomJpaRepository jpaRepo, AccountJpaRepository accountJpaRepository) {
        this.roomJpaRepository = jpaRepo;
        this.accountJpaRepository = accountJpaRepository;
    }

    @Override
    public Optional<Room> findById(Long id) {
        return roomJpaRepository.findById(id)
                .map(e -> {
                    Room r = Room.reconstitute(e.getId(), e.getName(), e.getDescription(), e.getCreatedBy().getId());
                    return r;
                });
    }

    @Override
    public Optional<Room> findByName(String name) {
        return roomJpaRepository.findByName(name)
                .map(e -> Room.reconstitute(e.getId(), e.getName(), e.getDescription(), e.getCreatedBy().getId()));
    }

    @Override
    public List<Room> findAll() {
        return roomJpaRepository.findAll().stream()
                .map(e -> Room.reconstitute(e.getId(), e.getName(), e.getDescription(), e.getCreatedBy().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByName(String name) {
        return roomJpaRepository.existsByName(name);
    }

    @Override
    public Room save(Room room) {
        // map createdBy UUID to AccountEntity
        AccountEntity accountEntity = accountJpaRepository.findById(room.getCreatedBy())
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + room.getCreatedBy()));
        RoomEntity entity;
        if (room.getId() != null) {
            entity = roomJpaRepository.findById(room.getId()).orElse(new RoomEntity());
            entity.setId(room.getId());
        } else {
            entity = new RoomEntity();
            entity.setCreatedBy(accountEntity);
        }
        entity.setName(room.getName());
        entity.setDescription(room.getDescription());
        // createdBy mapping must handle account entity; assume CreatedBy AccountEntity
        // loaded elsewhere?
        // Here we need AccountEntity; ideally room contains createdBy UUID; we re-fetch
        // AccountEntity
        // But for now, leave createdBy unchanged on updates
        RoomEntity saved = roomJpaRepository.save(entity);
        room.setId(saved.getId());
        return room;
    }
}
