package com.readingshare.room.dto;

import org.jspecify.annotations.NonNull;

/**
 * Data Transfer Object for Room.
 * Used for API responses to provide a clean and consistent interface.
 */
public record RoomResponse(
        Long id,
        @NonNull String name,
        String description) {
    // DTO constructor only - no fromDomain method to enforce service-layer
    // transformation
}
