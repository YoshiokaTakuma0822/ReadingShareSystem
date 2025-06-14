package com.readingshare.room.domain.repository;

import com.readingshare.room.domain.model.RoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IRoomMemberRepository extends JpaRepository<RoomMember, Long> {
    Optional<RoomMember> findByRoomIdAndUserId(Long roomId, Long userId);
    List<RoomMember> findByRoomId(Long roomId);
}