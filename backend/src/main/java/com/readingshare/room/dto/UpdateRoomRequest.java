package com.readingshare.room.dto;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 部屋情報更新APIのリクエストDTO
 */
public record UpdateRoomRequest(
        @JsonProperty("totalPages") Integer totalPages,
        @JsonProperty("genre") String genre,
        @JsonProperty("startTime") OffsetDateTime startTime,
        @JsonProperty("endTime") OffsetDateTime endTime) {
}
