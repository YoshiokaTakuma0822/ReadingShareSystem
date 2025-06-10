package com.readingshare.room.dto;

import org.jspecify.annotations.NonNull;

/**
 * Data Transfer Object for Member.
 * Used for API responses to provide a clean and consistent interface.
 */
public record MemberResponse(
        Long id,
        @NonNull String name,
        Long roomId) {
    // DTO constructor only - no fromDomain method to enforce service-layer
    // transformation
}
