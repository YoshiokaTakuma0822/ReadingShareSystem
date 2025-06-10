package com.readingshare.chat.dto;

import java.time.OffsetDateTime;

import org.jspecify.annotations.NonNull;

/**
 * Data Transfer Object for ChatMessage.
 * Used for API responses to provide a clean and consistent interface.
 */
public record MessageResponse(
        @NonNull Long id,
        @NonNull Long senderId,
        @NonNull Long roomId,
        @NonNull String content,
        @NonNull OffsetDateTime createdAt,
        @NonNull String type) {
    // DTO constructor only - no fromDomain method to enforce service-layer
    // transformation
}
