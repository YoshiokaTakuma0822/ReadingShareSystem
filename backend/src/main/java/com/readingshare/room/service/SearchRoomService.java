package com.readingshare.room.service;

import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.repository.IRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchRoomService {

    private final IRoomRepository roomRepository;

    @Autowired
    public SearchRoomService(IRoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> searchRooms(String keyword) {
        return roomRepository.findByRoomNameContaining(keyword);
    }
}