package com.readingshare.room.domain.repository;

import com.readingshare.room.domain.model.Room;

import java.util.List;

/**
 
高度な条件検索などを行うためのカスタムインターフェース*/
public interface IRoomRepositoryCustom {
    List<Room> findRecentRooms(int limit);
}