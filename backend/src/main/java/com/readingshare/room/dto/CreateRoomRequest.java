package com.readingshare.room.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * 部屋作成APIのリクエストDTO
 */
public record CreateRoomRequest(
        String roomName,
        UUID hostUserId,
        String bookTitle,
        String password,
        Integer maxPage,
        String genre,
        Instant startTime,
        Instant endTime,
        Integer pageSpeed
) {}
