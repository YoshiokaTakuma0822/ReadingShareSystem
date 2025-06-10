package com.readingshare.room.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readingshare.room.infrastructure.persistence.RoomEntity;

@Repository
public interface RoomJpaRepository extends JpaRepository<RoomEntity, Long> {
    @Override
    @NonNull
    <S extends RoomEntity> S save(S entity);

    @Override
    @NonNull
    List<RoomEntity> findAll();

    @Override
    @NonNull
    Optional<RoomEntity> findById(Long id);

    /**
     * Find room by name.
     */
    Optional<RoomEntity> findByName(String name);

    /**
     * Check if room exists by name.
     */
    boolean existsByName(String name);
}
