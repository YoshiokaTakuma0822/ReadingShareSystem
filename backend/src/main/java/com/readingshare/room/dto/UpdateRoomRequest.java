package com.readingshare.room.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 部屋情報更新APIのリクエストDTO
 */
public record UpdateRoomRequest(
        @JsonProperty("totalPages") Integer totalPages, // 追加: 本のページ数
        @JsonProperty("genre") String genre,      // 追加: 部屋のジャンル
        @JsonProperty("startTime")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime startTime, // 追加: 部屋の開始時刻
        @JsonProperty("endTime")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime endTime    // 追加: 部屋の終了時刻
) {
}
