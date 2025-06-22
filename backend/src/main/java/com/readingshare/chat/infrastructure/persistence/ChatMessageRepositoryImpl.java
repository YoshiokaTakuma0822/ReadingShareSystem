package com.readingshare.chat.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.readingshare.chat.domain.model.ChatMessage;
import com.readingshare.chat.domain.repository.IChatMessageRepository;

@Repository
public class ChatMessageRepositoryImpl implements IChatMessageRepository {
    @Autowired
    @Lazy
    private ChatMessageJpaRepository chatMessageRepository;

    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }

    @Override
    public List<ChatMessage> findByRoomId(UUID roomId) {
        return chatMessageRepository.findByRoomIdOrderBySentAtAsc(roomId);
    }

    @Override
    public Optional<ChatMessage> findById(UUID id) {
        return chatMessageRepository.findById(id);
    }
}
