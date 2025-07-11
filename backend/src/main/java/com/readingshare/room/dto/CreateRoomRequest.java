package com.readingshare.room.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 部屋作成APIのリクエストDTO
 */
public record CreateRoomRequest(
        String roomName,
        UUID hostUserId,
        String bookTitle,
        String password,
        Integer totalPages, // 追加: 本のページ数
        String genre, // 追加: 部屋のジャンル
        OffsetDateTime startTime, // 追加: 部屋の開始時刻
        OffsetDateTime endTime // 追加: 部屋の終了時刻
) {
}
