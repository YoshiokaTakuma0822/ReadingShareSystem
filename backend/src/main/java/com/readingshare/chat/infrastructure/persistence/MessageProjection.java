package com.readingshare.chat.infrastructure.persistence;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Projection interface for ChatMessage to optimize query performance.
 * Only loads necessary fields, reducing memory usage and improving query speed.
 */
public interface MessageProjection {
    Long getId();

    String getContent();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Asia/Tokyo")
    OffsetDateTime getCreatedAt();

    String getType();

    /**
     * Projection for User information to avoid N+1 query problem.
     */
    UserProjection getSender();

    /**
     * Projection for Room information.
     */
    RoomProjection getRoom();

    interface UserProjection {
        Long getId();

        String getName();

        AccountProjection getAccount();
    }

    /**
     * Projection for Account information.
     */
    interface AccountProjection {
        UUID getId();

        String getEmail();
    }

    /**
     * Projection for Room information.
     */
    interface RoomProjection {
        Long getId();

        String getName();
    }
}
