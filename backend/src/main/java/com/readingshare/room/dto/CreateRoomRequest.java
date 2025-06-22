package com.readingshare.room.dto;

import java.util.UUID;

/**
 * 部屋作成APIのリクエストDTO。
 *
 * @author 02004
 * @componentId C3
 * @moduleName ルーム作成リクエストDTO
 */
public record CreateRoomRequest(
        String roomName,
        UUID hostUserId,
        String bookTitle,
        String password) {
}
