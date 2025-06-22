package com.readingshare.room.dto;

import java.util.UUID;

/**
 * 部屋作成APIのリクエストDTO
 *
 * @author 23004
 */
public record CreateRoomRequest(
        String roomName,
        UUID hostUserId,
        String bookTitle,
        String password) {
}
