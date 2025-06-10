package com.readingshare.chat.dto;

import org.jspecify.annotations.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for ChatMessage.
 * Used for API requests and responses to provide a clean and consistent
 * interface.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MessageRequest(@NonNull @NotBlank String content) {
}
