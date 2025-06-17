package com.readingshare.chat.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.readingshare.chat.domain.model.UserProgress;
import com.readingshare.chat.domain.repository.IProgressRepository;

@Repository
public class ProgressRepositoryImpl implements IProgressRepository {
    @Autowired
    @Lazy
    private ProgressJpaRepository progressRepository;

    @Override
    public UserProgress save(UserProgress userProgress) {
        return progressRepository.save(userProgress);
    }

    @Override
    public List<UserProgress> findByRoomId(UUID roomId) {
        return progressRepository.findByRoomIdOrderByUserIdAsc(roomId);
    }

    @Override
    public Optional<UserProgress> findByRoomIdAndUserId(UUID roomId, UUID userId) {
        return progressRepository.findByRoomIdAndUserId(roomId, userId);
    }
}
