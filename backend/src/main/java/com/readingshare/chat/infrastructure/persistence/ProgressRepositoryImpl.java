package com.readingshare.chat.infrastructure.persistence;

import com.readingshare.chat.domain.model.UserProgress;
import com.readingshare.chat.domain.repository.IProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProgressRepositoryImpl implements IProgressRepository {
    @Autowired
    @Lazy
    private ProgressRepository progressRepository;

    @Override
    public UserProgress save(UserProgress userProgress) {
        return progressRepository.save(userProgress);
    }

    @Override
    public List<UserProgress> findByRoomId(Long roomId) {
        return progressRepository.findByRoomIdOrderByUserIdAsc(roomId);
    }

    @Override
    public Optional<UserProgress> findByRoomIdAndUserId(Long roomId, Long userId) {
        return progressRepository.findByRoomIdAndUserId(roomId, userId);
    }
}
