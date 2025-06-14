package com.readingshare.room.infrastructure.persistence;

import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.repository.IRoomRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RoomRepository のカスタム実装
 */
@Repository
public class RoomRepositoryImpl implements IRoomRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 最近作成された部屋を新しい順に取得（例としてのカスタムクエリ）
     */
    @Override
    public List<Room> findRecentRooms(int limit) {
        String jpql = "SELECT r FROM Room r ORDER BY r.createdAt DESC";
        TypedQuery<Room> query = entityManager.createQuery(jpql, Room.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
}