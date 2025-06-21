package com.readingshare.room.service;

import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.repository.IRoomRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateRoomService {

    private final IRoomRepository roomRepository;

    @Autowired
    public CreateRoomService(IRoomRepository roomRepository) {
        this.roomRepository = roomRepository;
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

        return savedRoom;
    }

    @Transactional
    public void deleteRoom(Long roomId, Long userId) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // ホストのみ削除可能
        if (!room.getHostUserId().equals(userId)) {
            throw new SecurityException("Only the host can delete the room.");
        }

        roomRepository.delete(room);
    }
}
