package com.readingshare.room.dto;

public record UpdateRoomRequest(
    Integer maxPage,
    String genre,
    java.time.Instant startTime,
    java.time.Instant endTime,
    Integer pageSpeed
) {}
