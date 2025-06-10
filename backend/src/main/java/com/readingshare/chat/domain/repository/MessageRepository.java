package com.readingshare.chat.domain.repository;

import java.util.List;
import java.util.Optional;

import com.readingshare.chat.domain.model.Message;

/**
 * Domain repository interface for Message operations.
 */
public interface MessageRepository {

    /**
     * Save a message to the repository.
     *
     * @param message the message to save
     * @return the saved message with assigned ID
     */
    Message save(Message message);

    /**
     * Find a message by its ID.
     *
     * @param id the message ID
     * @return the message if found
     */
    Optional<Message> findById(Long id);

    /**
     * Find messages by room ID, ordered by creation date (newest first).
     *
     * @param roomId the room ID
     * @param limit  maximum number of messages to return
     * @return list of messages
     */
    List<Message> findByRoomIdOrderByCreatedAtDesc(Long roomId, int limit);

    /**
     * Find messages by room ID before a specific message ID.
     *
     * @param roomId   the room ID
     * @param beforeId the message ID to search before
     * @param limit    maximum number of messages to return
     * @return list of messages
     */
    List<Message> findByRoomIdAndIdLessThanOrderByCreatedAtDesc(Long roomId, Long beforeId, int limit);

    /**
     * Find messages by sender member ID.
     *
     * @param memberId the member ID
     * @param limit    maximum number of messages to return
     * @return list of messages
     */
    List<Message> findByMemberIdOrderByCreatedAtDesc(Long memberId, int limit);

    /**
     * Find all messages ordered by creation date (newest first).
     *
     * @param limit maximum number of messages to return
     * @return list of messages
     */
    List<Message> findAllOrderByCreatedAtDesc(int limit);

    /**
     * Find messages before a specific ID, ordered by creation date (newest first).
     *
     * @param beforeId the message ID to search before
     * @param limit    maximum number of messages to return
     * @return list of messages
     */
    List<Message> findByIdLessThanOrderByCreatedAtDesc(Long beforeId, int limit);
}
