package com.readingshare.account.dto;

import jakarta.validation.constraints.NotBlank;

public record AccountLoginRequest(@NotBlank String email, @NotBlank String password) {
}
