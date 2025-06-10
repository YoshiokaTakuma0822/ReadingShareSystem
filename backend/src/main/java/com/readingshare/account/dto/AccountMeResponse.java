package com.readingshare.account.dto;

import java.time.Instant;
import java.util.UUID;

public record AccountMeResponse(UUID id, String email, Instant accessTokenExpiresAt) {
}
