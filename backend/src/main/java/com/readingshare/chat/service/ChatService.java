package com.readingshare.chat.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.chat.domain.model.Message;
import com.readingshare.chat.domain.repository.MessageRepository;
import com.readingshare.chat.dto.MessageResponse;
import com.readingshare.room.domain.model.Member;
import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.repository.MemberRepository;
import com.readingshare.room.domain.repository.RoomRepository;
import com.readingshare.room.dto.MemberResponse;
import com.readingshare.room.service.ActiveMembersService;

/**
 * Service class for handling chat-related business logic.
 * Follows DDD principles by using domain repositories only.
 */
@Service
public class ChatService {
    private final ActiveMembersService activeUsersService;
    private final MemberRepository memberRepository;
    private final MessageRepository messageRepository;
    private final RoomRepository roomRepository;

    public ChatService(
            ActiveMembersService activeUsersService,
            MemberRepository memberRepository,
            MessageRepository messageRepository,
            RoomRepository roomRepository) {
        this.activeUsersService = activeUsersService;
        this.memberRepository = memberRepository;
        this.messageRepository = messageRepository;
        this.roomRepository = roomRepository;
    }

    /**
     * Creates a new chat message in a room using the room's domain method.
     *
     * @param content  the message content
     * @param roomId   the ID of the room
     * @param memberId the ID of the sending member
     * @return the created message as DTO
     * @throws IllegalArgumentException if the room or member is not found
     */
    @Transactional
    public @NonNull MessageResponse createMessageInRoom(
            @NonNull String content,
            @NonNull Long roomId,
            @NonNull Long memberId) {
        // Find sender using domain repository
        Member sender = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found with id: " + memberId));

        // Find room using domain repository
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + roomId));

        // Create message using domain model (room aggregate)
        Message message = room.createMessage(content, sender);

        // Save message using domain repository
        Message savedMessage = messageRepository.save(message);

        return toDtoFromDomain(savedMessage);
    }

    /**
     * Fetch chat messages for a specific room. If beforeId is provided, returns
     * messages before that ID.
     * If beforeId is null, returns the most recent messages.
     *
     * @param roomId   the ID of the room to fetch messages from
     * @param beforeId the ID to fetch messages before (null for recent messages)
     * @param limit    maximum number of messages to return
     * @return list of messages as DTOs
     */
    @Transactional(readOnly = true)
    public @NonNull List<MessageResponse> getMessagesBeforeAsDto(Long roomId, @Nullable Long beforeId, int limit) {
        List<Message> messages = beforeId == null
                ? messageRepository.findByRoomIdOrderByCreatedAtDesc(roomId, limit)
                : messageRepository.findByRoomIdAndIdLessThanOrderByCreatedAtDesc(roomId, beforeId, limit);

        return Objects.requireNonNull(messages.stream()
                .map(this::toDtoFromDomain)
                .collect(Collectors.toList()));
    }

    /**
     * Fetch chat messages. If beforeId is provided, returns messages before that
     * ID.
     * If beforeId is null, returns the most recent messages.
     *
     * @param beforeId the ID to fetch messages before (null for recent messages)
     * @param limit    maximum number of messages to return
     * @return list of messages as DTOs
     */
    @Transactional(readOnly = true)
    public @NonNull List<MessageResponse> getMessagesBeforeAsDto(Long beforeId, int limit) {
        List<Message> messages = beforeId == null
                ? messageRepository.findAllOrderByCreatedAtDesc(limit)
                : messageRepository.findByIdLessThanOrderByCreatedAtDesc(beforeId, limit);

        return Objects.requireNonNull(messages.stream()
                .map(this::toDtoFromDomain)
                .collect(Collectors.toList()));
    }

    /**
     * Fetch chat messages by member ID.
     *
     * @param memberId the ID of the member whose messages to fetch
     * @param limit    maximum number of messages to return
     * @return list of messages as DTOs
     */
    @Transactional(readOnly = true)
    public @NonNull List<MessageResponse> getMessagesByMemberIdAsDto(Long memberId, int limit) {
        List<Message> messages = messageRepository.findByMemberIdOrderByCreatedAtDesc(memberId, limit);

        return Objects.requireNonNull(messages.stream()
                .map(this::toDtoFromDomain)
                .collect(Collectors.toList()));
    }

    /**
     * Gets the list of currently active users.
     *
     * @return list of active users as DTOs
     */
    @Transactional(readOnly = true)
    public @NonNull List<MemberResponse> getActiveUsersAsDto() {
        return activeUsersService.getActiveUsers();
    }

    /**
     * Convert a domain Message to DTO.
     */
    private MessageResponse toDtoFromDomain(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getSender().getId(),
                message.getRoom().getId(),
                message.getContent(),
                message.getCreatedAt(),
                message.getType().name());
    }
}
