package com.readingshare.room.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 部屋作成APIのリクエストDTO
 */
public record CreateRoomRequest(
        String roomName,
        UUID hostUserId,
        String bookTitle,
        String password,
        Integer totalPages,      // 追加: 本のページ数
        String genre,            // 追加: 部屋のジャンル
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime startTime, // 追加: 部屋の開始時刻
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime endTime    // 追加: 部屋の終了時刻
) {
}
