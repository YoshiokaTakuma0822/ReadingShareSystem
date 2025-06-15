package com.readingshare.room.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.readingshare.room.domain.model.RoomMember;
import com.readingshare.room.domain.repository.IRoomMemberRepository;

@Repository
public class RoomMemberRepositoryImpl implements IRoomMemberRepository {
    @Autowired
    @Lazy
    private RoomMemberRepository roomMemberRepository;

    @Override
    public RoomMember save(RoomMember roomMember) {
        return roomMemberRepository.save(roomMember);
    }

    @Override
    public List<RoomMember> findByRoomId(UUID roomId) {
        return roomMemberRepository.findByRoomId(roomId);
    }

    @Override
    public Optional<RoomMember> findByRoomIdAndUserId(UUID roomId, UUID userId) {
        return roomMemberRepository.findByRoomIdAndUserId(roomId, userId);
    }
}
