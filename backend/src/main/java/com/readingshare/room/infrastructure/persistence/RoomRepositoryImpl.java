package com.readingshare.room.infrastructure.persistence;

<<<<<<< HEAD
import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.repository.IRoomRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
=======
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
>>>>>>> origin/dev2

import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.repository.IRoomRepository;

<<<<<<< HEAD
// @Repository アノテーションは省略可能（Spring Data のルールに従う場合）
public class RoomRepositoryImpl implements IRoomRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

=======
@Repository
public class RoomRepositoryImpl implements IRoomRepository {
    @Autowired
    @Lazy
    private RoomJpaRepository roomRepository;

>>>>>>> origin/dev2
    @Override
    public Room save(Room room) {
        return roomRepository.save(room);
    }
<<<<<<< HEAD
=======

    @Override
    public Optional<Room> findById(UUID id) {
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

    @Override
    public Page<Room> findAll(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }
>>>>>>> origin/dev2
}
