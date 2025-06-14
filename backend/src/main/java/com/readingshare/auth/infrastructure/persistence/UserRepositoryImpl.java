package com.readingshare.auth.infrastructure.persistence;

import com.readingshare.auth.domain.model.User;
import com.readingshare.auth.domain.model.UserId;
import com.readingshare.auth.domain.repository.IUserRepository;
import com.readingshare.common.exception.DatabaseAccessException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * IUserRepository の JPA/Hibernate 実装。
 * 担当: 小亀
 */
@Repository
public class UserRepositoryImpl implements IUserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public User save(User user) {
        try {
            if (user.getId() == null) {
                entityManager.persist(user);
                return user;
            } else {
                return entityManager.merge(user);
            }
        } catch (Exception e) {
            throw new DatabaseAccessException("Failed to save user.", e);
        }
    }

    @Override
    public Optional<User> findById(UserId id) {
        try {
            return Optional.ofNullable(entityManager.find(User.class, id.getValue()));
        } catch (Exception e) {
            throw new DatabaseAccessException("Failed to find user by ID: " + id.getValue(), e);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try {
            TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new DatabaseAccessException("Failed to find user by username: " + username, e);
        }
    }
}