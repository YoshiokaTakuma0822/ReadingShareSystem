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
    private RoomMemberJpaRepository roomMemberRepository;

    @Override
    public RoomMember save(RoomMember roomMember) {
        return roomMemberRepository.save(roomMember);
    }

    @Override
    public List<RoomMember> findByRoom(com.readingshare.room.domain.model.Room room) {
        return roomMemberRepository.findByRoom(room);
    }

    @Override
    public Optional<RoomMember> findByRoomAndUserId(com.readingshare.room.domain.model.Room room, UUID userId) {
        return roomMemberRepository.findByRoomAndUserId(room, userId);
    }

    @Override
    public void delete(RoomMember roomMember) {
        roomMemberRepository.delete(roomMember);
    }
}
