package com.readingshare.room.service;

import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.model.RoomMember;
import com.readingshare.room.domain.repository.IRoomRepository;
import com.readingshare.room.domain.repository.IRoomMemberRepository;
import com.readingshare.room.service.dto.CreateRoomRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            request.getHostUserId(),
            request.getTotalPages(),
            request.getPageTurnSpeed(),
            request.getGenre(),         // 追加
            request.getStartTime(),     // 追加
            request.getEndTime()        // 追加
        );
        Room savedRoom = roomRepository.save(room);

        // ホストユーザーを RoomMember として登録
        RoomMember member = new RoomMember();
        member.setRoom(savedRoom);
        member.setUserId(request.getHostUserId()); // Long型で渡す
        roomMemberRepository.save(member);
        // 必要に応じて他の初期化処理をここに追加できます
        return savedRoom;
    }

    @Transactional
    public void deleteRoom(Long roomId, Long userId) {
        // Convert Long to UUID (assuming roomId is stored as a string representation of UUID)
        java.util.UUID uuidRoomId = java.util.UUID.fromString(roomId.toString());
        Room room = roomRepository.findById(uuidRoomId)
            .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // ホストのみ削除可能
        if (!room.getHostUserId().equals(userId)) {
            throw new SecurityException("Only the host can delete the room.");
        }

        roomRepository.delete(room);
    }
}
