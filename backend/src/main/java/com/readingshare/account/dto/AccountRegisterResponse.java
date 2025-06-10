package com.readingshare.account.dto;

import java.util.UUID;

public record AccountRegisterResponse(UUID id, String email) {
}
