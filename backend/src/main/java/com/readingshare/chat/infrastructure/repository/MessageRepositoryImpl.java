package com.readingshare.chat.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.readingshare.chat.domain.model.Message;
import com.readingshare.chat.domain.repository.MessageRepository;
import com.readingshare.chat.infrastructure.persistence.MessageEntity;
import com.readingshare.chat.infrastructure.persistence.MessageProjection;
import com.readingshare.room.domain.model.Member;
import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.repository.MemberRepository;
import com.readingshare.room.domain.repository.RoomRepository;
import com.readingshare.room.infrastructure.persistence.MemberEntity;
import com.readingshare.room.infrastructure.persistence.RoomEntity;

/**
 * Implementation of MessageRepository using JPA.
 */
@Repository
public class MessageRepositoryImpl implements MessageRepository {

    private final MessageJpaRepository messageJpaRepository;
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;

    public MessageRepositoryImpl(MessageJpaRepository messageJpaRepository,
            MemberRepository memberRepository,
            RoomRepository roomRepository) {
        this.messageJpaRepository = messageJpaRepository;
        this.memberRepository = memberRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public Message save(Message message) {
        MessageEntity entity = toEntity(message);
        MessageEntity savedEntity = messageJpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Message> findById(Long id) {
        return messageJpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<Message> findByRoomIdOrderByCreatedAtDesc(Long roomId, int limit) {
        return messageJpaRepository.findByRoomIdOrderByCreatedAtDescIdDesc(roomId, PageRequest.of(0, limit))
                .stream()
                .map(this::projectionToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findByRoomIdAndIdLessThanOrderByCreatedAtDesc(Long roomId, Long beforeId, int limit) {
        return messageJpaRepository
                .findByRoomIdAndIdLessThanOrderByCreatedAtDescIdDesc(roomId, beforeId, PageRequest.of(0, limit))
                .stream()
                .map(this::projectionToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findByMemberIdOrderByCreatedAtDesc(Long memberId, int limit) {
        return messageJpaRepository.findBySenderIdOrderByCreatedAtDescIdDesc(memberId, PageRequest.of(0, limit))
                .stream()
                .map(this::projectionToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findAllOrderByCreatedAtDesc(int limit) {
        return messageJpaRepository.findAllByOrderByCreatedAtDescIdDesc(PageRequest.of(0, limit))
                .stream()
                .map(this::projectionToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findByIdLessThanOrderByCreatedAtDesc(Long beforeId, int limit) {
        return messageJpaRepository.findByIdLessThanOrderByCreatedAtDescIdDesc(beforeId, PageRequest.of(0, limit))
                .stream()
                .map(this::projectionToDomain)
                .collect(Collectors.toList());
    }

    private MessageEntity toEntity(Message message) {
        MessageEntity entity = new MessageEntity();
        if (message.getId() != null) {
            entity.setId(message.getId());
        }
        entity.setContent(message.getContent());
        entity.setCreatedAt(message.getCreatedAt());
        entity.setType(MessageEntity.MessageType.valueOf(message.getType().name()));

        // Convert sender to entity - for saving, we need to find or create the entity
        if (message.getSender() != null) {
            MemberEntity senderEntity = new MemberEntity();
            senderEntity.setId(message.getSender().getId());
            senderEntity.setName(message.getSender().getName());
            entity.setSender(senderEntity);
        }

        // Convert room to entity
        if (message.getRoom() != null) {
            RoomEntity roomEntity = new RoomEntity();
            roomEntity.setId(message.getRoom().getId());
            roomEntity.setName(message.getRoom().getName());
            roomEntity.setDescription(message.getRoom().getDescription());
            entity.setRoom(roomEntity);
        }

        return entity;
    }

    private Message toDomain(MessageEntity entity) {
        // Convert sender to domain - use member repository to get the full member
        Member sender = null;
        if (entity.getSender() != null) {
            sender = memberRepository.findById(entity.getSender().getId()).orElse(null);
        }

        // Convert room to domain
        Room room = null;
        if (entity.getRoom() != null) {
            room = roomRepository.findById(entity.getRoom().getId()).orElse(null);
        }

        // If sender is null, we can't create a valid Message domain object
        if (sender == null) {
            throw new IllegalStateException("Message sender not found for message ID: " + entity.getId() +
                    ", sender entity ID: " + (entity.getSender() != null ? entity.getSender().getId() : "null"));
        }

        if (room == null) {
            throw new IllegalStateException("Message room not found for message ID: " + entity.getId() +
                    ", room entity ID: " + (entity.getRoom() != null ? entity.getRoom().getId() : "null"));
        }

        return Message.reconstitute(
                entity.getId(),
                entity.getContent(),
                sender,
                room,
                entity.getCreatedAt(),
                Message.MessageType.valueOf(entity.getType().name()));
    }

    private Message projectionToDomain(MessageProjection projection) {
        // For projections, we'll need to resolve the full domain objects
        Member sender = null;
        if (projection.getSender() != null) {
            // Load sender from member repository
            sender = memberRepository.findById(projection.getSender().getId()).orElse(null);
        }

        Room room = null;
        if (projection.getRoom() != null) {
            room = roomRepository.findById(projection.getRoom().getId()).orElse(null);
        }

        // If sender is null, we can't create a valid Message domain object
        // This should not happen in normal cases, but if it does, we should handle it
        // gracefully
        if (sender == null) {
            throw new IllegalStateException("Message sender not found for message ID: " + projection.getId());
        }

        if (room == null) {
            throw new IllegalStateException("Message room not found for message ID: " + projection.getId());
        }

        return Message.reconstitute(
                projection.getId(),
                projection.getContent(),
                sender,
                room,
                projection.getCreatedAt(),
                Message.MessageType.valueOf(projection.getType()));
    }
}
