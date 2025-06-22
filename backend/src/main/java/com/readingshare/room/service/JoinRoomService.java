package com.readingshare.room.service;

import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.model.RoomMember;
import com.readingshare.room.domain.repository.IRoomMemberRepository;
import com.readingshare.room.domain.repository.IRoomRepository;
import com.readingshare.room.service.dto.JoinRoomRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class JoinRoomService {

    private final IRoomMemberRepository roomMemberRepository;
    private final IRoomRepository roomRepository;

    @Autowired
    public JoinRoomService(IRoomMemberRepository roomMemberRepository, IRoomRepository roomRepository) {
        this.roomMemberRepository = roomMemberRepository;
        this.roomRepository = roomRepository;
    }

    @Transactional
    public RoomMember joinRoom(JoinRoomRequest request) {
        // 既に参加済みか確認
        UUID roomId = UUID.fromString(request.getRoomId());
        UUID userId = UUID.fromString(request.getUserId());
        roomMemberRepository.findByRoomIdAndUserId(roomId, userId)
            .ifPresent(existing -> {
                throw new IllegalStateException("User already joined the room.");
            });

        // 参加処理
        RoomMember member = new RoomMember(roomId, userId, Instant.now());
        return roomMemberRepository.save(member);
    }

    public List<Room> findRecentRoomsByUserId(String userId, int limit) {
        UUID uuid = UUID.fromString(userId);
        List<UUID> roomIds = roomMemberRepository.findRecentRoomIdsByUserId(uuid, PageRequest.of(0, limit));
        return roomRepository.findAllById(roomIds);
    }
}