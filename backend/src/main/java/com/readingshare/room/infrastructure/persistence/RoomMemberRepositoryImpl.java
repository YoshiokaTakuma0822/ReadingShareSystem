package com.readingshare.room.infrastructure.persistence;

import com.readingshare.room.domain.model.RoomMember;
import com.readingshare.room.domain.repository.IRoomMemberRepository;
import com.readingshare.common.exception.DatabaseAccessException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * IRoomMemberRepository の JPA/Hibernate 実装。
 * 担当: 芳岡
 */
@Repository
public class RoomMemberRepositoryImpl implements IRoomMemberRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public RoomMember save(RoomMember roomMember) {
        try {
            if (roomMember.getId() == null) {
                entityManager.persist(roomMember);
                return roomMember;
            } else {
                return entityManager.merge(roomMember);
            }
        } catch (Exception e) {
            throw new DatabaseAccessException("Failed to save room member.", e);
        }
    }

    @Override
    public List<RoomMember> findByRoomId(Long roomId) {
        try {
            TypedQuery<RoomMember> query = entityManager.createQuery(
                "SELECT rm FROM RoomMember rm WHERE rm.roomId = :roomId", RoomMember.class);
            query.setParameter("roomId", roomId);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseAccessException("Failed to find room members by room ID: " + roomId, e);
        }
    }

    @Override
    public Optional<RoomMember> findByRoomIdAndUserId(Long roomId, Long userId) {
        try {
            TypedQuery<RoomMember> query = entityManager.createQuery(
                "SELECT rm FROM RoomMember rm WHERE rm.roomId = :roomId AND rm.userId = :userId", RoomMember.class);
            query.setParameter("roomId", roomId);
            query.setParameter("userId", userId);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new DatabaseAccessException("Failed to find room member by room ID and user ID: room=" + roomId + ", user=" + userId, e);
        }
    }
}