package com.readingshare.chat.infrastructure.persistence;

import com.readingshare.chat.domain.model.UserProgress;
import com.readingshare.chat.domain.repository.IProgressRepository;
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
 * IProgressRepository の JPA/Hibernate 実装。
 * 担当: 榎本
 */
@Repository
public class ProgressRepositoryImpl implements IProgressRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public UserProgress save(UserProgress userProgress) {
        try {
            if (userProgress.getId() == null) {
                entityManager.persist(userProgress);
                return userProgress;
            } else {
                return entityManager.merge(userProgress);
            }
        } catch (Exception e) {
            throw new DatabaseAccessException("Failed to save user progress.", e);
        }
    }

    @Override
    public List<UserProgress> findByRoomId(Long roomId) {
        try {
            TypedQuery<UserProgress> query = entityManager.createQuery(
                "SELECT up FROM UserProgress up WHERE up.roomId = :roomId ORDER BY up.userId ASC", UserProgress.class);
            query.setParameter("roomId", roomId);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseAccessException("Failed to find user progress by room ID: " + roomId, e);
        }
    }

    @Override
    public Optional<UserProgress> findByRoomIdAndUserId(Long roomId, Long userId) {
        try {
            TypedQuery<UserProgress> query = entityManager.createQuery(
                "SELECT up FROM UserProgress up WHERE up.roomId = :roomId AND up.userId = :userId", UserProgress.class);
            query.setParameter("roomId", roomId);
            query.setParameter("userId", userId);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new DatabaseAccessException("Failed to find user progress by room ID and user ID: room=" + roomId + ", user=" + userId, e);
        }
    }
}
