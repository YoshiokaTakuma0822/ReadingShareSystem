package com.readingshare.state.dto;

/**
 * ユーザーの読書状態更新リクエストのDTO
 */
public record UpdateUserReadingStateRequest(
        String userId,
        int currentPage,
        String comment) {
}
