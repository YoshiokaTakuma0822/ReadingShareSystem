package com.readingshare.room.dto;

import java.time.LocalDateTime;
import java.time.Instant;
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
        Integer totalPages,      // 本のページ数
        Integer maxPage,         // 最大ページ数（dev_al23060_merge_test）
        String genre,            // 部屋のジャンル
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime startTime, // 開始時刻
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime endTime,   // 終了時刻
        Integer pageSpeed        // ページめくり速度（dev_al23060_merge_test）
) {
}
