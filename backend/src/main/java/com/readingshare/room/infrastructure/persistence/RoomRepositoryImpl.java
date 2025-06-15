package com.readingshare.room.infrastructure.persistence;

import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.repository.IRoomRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

// @Repository アノテーションは省略可能（Spring Data のルールに従う場合）
public class RoomRepositoryImpl implements IRoomRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Room> findRecentRooms(int limit) {
        String jpql = "SELECT r FROM Room r ORDER BY r.createdAt DESC";
        TypedQuery<Room> query = entityManager.createQuery(jpql, Room.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
}
