package com.readingshare.room.service;

import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.model.RoomMember;
import com.readingshare.room.domain.repository.IRoomRepository;
import com.readingshare.room.domain.repository.IRoomMemberRepository;
import com.readingshare.room.service.dto.CreateRoomRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreateRoomService {

    private final IRoomRepository roomRepository;
    private final IRoomMemberRepository roomMemberRepository;

    @Autowired
    public CreateRoomService(IRoomRepository roomRepository, IRoomMemberRepository roomMemberRepository) {
        this.roomRepository = roomRepository;
        this.roomMemberRepository = roomMemberRepository;
    }

    @Transactional
    public Room createRoom(CreateRoomRequest request) {
        // 部屋名の重複チェック
        roomRepository.findByRoomName(request.getRoomName()).ifPresent(existing -> {
            throw new IllegalArgumentException("Room name already exists: " + request.getRoomName());
        });

        // Room を作成・保存
        Room room = new Room(
            request.getRoomName(),
            request.getBookTitle(),
            UUID.fromString(request.getHostUserId()),
            request.getTotalPages(),
            request.getGenre(),
            request.getStartTime(),
            request.getEndTime(),
            request.getPageTurnSpeed()
        );
        Room savedRoom = roomRepository.save(room);

        // ホストユーザーを RoomMember として登録
        RoomMember member = new RoomMember(savedRoom.getId(), UUID.fromString(request.getHostUserId()));
        roomMemberRepository.save(member);

        return savedRoom;
    }

    @Transactional
    public void deleteRoom(UUID roomId, UUID userId) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // ホストのみ削除可能
        if (!room.getHostUserId().equals(userId)) {
            throw new SecurityException("Only the host can delete the room.");
        }

        roomRepository.delete(room);
    }
}
