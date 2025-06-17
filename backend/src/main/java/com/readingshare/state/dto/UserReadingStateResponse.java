package com.readingshare.state.dto;

/**
 * ユーザーの読書状態レスポンスのDTO
 */
public record UserReadingStateResponse(
        String userId,
        int currentPage,
        String comment) {
}
