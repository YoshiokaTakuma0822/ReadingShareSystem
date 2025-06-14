package com.readingshare.room.service;

import com.readingshare.room.domain.model.RoomMember;
import com.readingshare.room.domain.repository.IRoomMemberRepository;
import com.readingshare.room.service.dto.JoinRoomRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JoinRoomService {

    private final IRoomMemberRepository roomMemberRepository;

    @Autowired
    public JoinRoomService(IRoomMemberRepository roomMemberRepository) {
        this.roomMemberRepository = roomMemberRepository;
    }

    @Transactional
    public RoomMember joinRoom(JoinRoomRequest request) {
        // 既に参加済みか確認
        roomMemberRepository.findByRoomIdAndUserId(request.getRoomId(), request.getUserId())
            .ifPresent(existing -> {
                throw new IllegalStateException("User already joined the room.");
            });

        // 参加処理
        RoomMember member = new RoomMember(request.getRoomId(), request.getUserId());
        return roomMemberRepository.save(member);
    }
}