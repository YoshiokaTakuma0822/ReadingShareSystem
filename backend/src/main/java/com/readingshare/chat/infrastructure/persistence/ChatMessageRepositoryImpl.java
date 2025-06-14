package com.readingshare.chat.infrastructure.persistence;

import com.readingshare.chat.domain.model.ChatMessage;
import com.readingshare.chat.domain.repository.IChatMessageRepository;
import com.readingshare.common.exception.DatabaseAccessException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * IChatMessageRepository の JPA/Hibernate 実装。
 * 担当: 榎本
 */
@Repository
public class ChatMessageRepositoryImpl implements IChatMessageRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public ChatMessage save(ChatMessage chatMessage) {
        try {
            if (chatMessage.getId() == null) {
                entityManager.persist(chatMessage);
                return chatMessage;
            } else {
                return entityManager.merge(chatMessage);
            }
        } catch (Exception e) {
            throw new DatabaseAccessException("Failed to save chat message.", e);
        }
    }

    @Override
    public List<ChatMessage> findByRoomId(Long roomId) {
        try {
            TypedQuery<ChatMessage> query = entityManager.createQuery(
                "SELECT cm FROM ChatMessage cm WHERE cm.roomId = :roomId ORDER BY cm.sentAt ASC", ChatMessage.class);
            query.setParameter("roomId", roomId);
            return query.getResultList();
        } catch (Exception e) {
            throw new DatabaseAccessException("Failed to find chat messages by room ID: " + roomId, e);
        }
    }

    @Override
    public Optional<ChatMessage> findById(Long id) {
        try {
            return Optional.ofNullable(entityManager.find(ChatMessage.class, id));
        } catch (Exception e) {
            throw new DatabaseAccessException("Failed to find chat message by ID: " + id, e);
        }
    }
}
