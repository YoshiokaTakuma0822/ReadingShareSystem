package com.readingshare.room.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 部屋情報更新APIのリクエストDTO
 */
public record UpdateRoomRequest(
        @JsonProperty("totalPages") Integer totalPages,
        @JsonProperty("genre") String genre,
        @JsonProperty("startTime") @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startTime,
        @JsonProperty("endTime") @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime endTime) {
}
