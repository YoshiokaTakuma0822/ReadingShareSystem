package com.readingshare.room.infrastructure.persistence;

import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.model.RoomId;
import com.readingshare.room.domain.repository.IRoomRepository;
import com.readingshare.common.exception.DatabaseAccessException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * IRoomRepository の JPA/Hibernate 実装。
 * 担当: 芳岡
 */
@Repository
public class RoomRepositoryImpl implements IRoomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Room save(Room room) {
        try {
            if (room.getId() == null) {
                entityManager.persist(room);
                return room;
            } else {
                return entityManager.merge(room);
            }
        } catch (Exception e) {
            throw new DatabaseAccessException("Failed to save room.", e);
        }
    }

    @Override
    public Optional<Room> findById(RoomId id) {
        try {
            return Optional.ofNullable(entityManager.find(Room.class, id.getValue()));
        } catch (Exception e) {
            throw new DatabaseAccessException("Failed to find room by ID: " + id.getValue(), e);
        }
    }

    @Override
    public List<Room> findByKeyword(String keyword) {
        try {
            TypedQuery<Room> query = entityManager.createQuery(
                "SELECT r FROM Room r WHERE r.roomName LIKE :keyword OR r.bookTitle LIKE :keyword", Room.class);
            query.setParameter("keyword", "%" + keyword + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseAccessException("Failed to search rooms by keyword: " + keyword, e);
        }
    }

    @Override
    public List<Room> findAll() {
        try {
            TypedQuery<Room> query = entityManager.createQuery("SELECT r FROM Room r", Room.class);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseAccessException("Failed to retrieve all rooms.", e);
        }
    }
}