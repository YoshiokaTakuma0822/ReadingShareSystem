package com.readingshare.state.dto;

/**
 * ユーザーの読書状態レスポンスのDTO
 *
 * @author 02003
 * @componentId C4
 */
public record UserReadingStateResponse(
        String userId,
        int currentPage,
        String comment) {
}
