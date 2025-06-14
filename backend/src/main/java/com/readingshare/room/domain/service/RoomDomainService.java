package com.readingshare.room.domain.service;

import com.readingshare.room.domain.repository.IRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomDomainService {

    private final IRoomRepository roomRepository;

    @Autowired
    public RoomDomainService(IRoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    /**
     * 部屋名が既に存在するか判定
     */
    public boolean isRoomNameDuplicated(String roomName) {
        return roomRepository.findByRoomName(roomName).isPresent();
    }
}