package com.readingshare.room.service;

import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.model.RoomMember;
import com.readingshare.room.domain.repository.IRoomMemberRepository;
import com.readingshare.room.domain.repository.IRoomRepository;
import com.readingshare.room.service.dto.JoinRoomRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        roomMemberRepository.findByRoomIdAndUserId(request.getRoomId(), request.getUserId())
            .ifPresent(existing -> {
                throw new IllegalStateException("User already joined the room.");
            });

        // 参加処理
        RoomMember member = new RoomMember(request.getRoomId(), request.getUserId());
        return roomMemberRepository.save(member);
    }

    public List<Room> findRecentRoomsByUserId(Long userId, int limit) {
        // RoomMemberからuserIdで参加履歴を新しい順で取得し、Roomを返す
        List<Long> roomIds = roomMemberRepository.findRecentRoomIdsByUserId(userId, limit);
        return roomRepository.findAllById(roomIds);
    }
}