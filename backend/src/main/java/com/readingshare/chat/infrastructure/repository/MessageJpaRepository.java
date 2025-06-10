package com.readingshare.chat.infrastructure.repository;

import java.util.List;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readingshare.chat.infrastructure.persistence.MessageEntity;
import com.readingshare.chat.infrastructure.persistence.MessageProjection;

@Repository
public interface MessageJpaRepository extends JpaRepository<MessageEntity, Long> {
    @Override
    @NonNull
    <S extends MessageEntity> S save(S entity);

    @Override
    @NonNull
    List<MessageEntity> findAll();

    /**
     * Get recent messages using projection to optimize query performance.
     * This avoids loading unnecessary entity data and reduces memory usage.
     */
    @NonNull
    List<MessageProjection> findAllByOrderByCreatedAtDescIdDesc(Pageable pageable);

    /**
     * Get recent messages for a specific room using projection.
     */
    @NonNull
    List<MessageProjection> findByRoomIdOrderByCreatedAtDescIdDesc(Long roomId, Pageable pageable);

    /**
     * Fetch messages older than the given ID using projection, ordered
     * newest-first, limited by
     * pageable.
     */
    @NonNull
    List<MessageProjection> findByIdLessThanOrderByCreatedAtDescIdDesc(Long beforeId, Pageable pageable);

    /**
     * Fetch messages older than the given ID for a specific room using projection.
     */
    @NonNull
    List<MessageProjection> findByRoomIdAndIdLessThanOrderByCreatedAtDescIdDesc(Long roomId, Long beforeId,
            Pageable pageable);

    /**
     * Find all messages by sender member ID using projection.
     */
    @NonNull
    List<MessageProjection> findBySenderIdOrderByCreatedAtDescIdDesc(Long memberId, Pageable pageable);
}
