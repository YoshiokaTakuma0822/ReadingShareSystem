package com.readingshare.state.dto;

/**
 * ユーザーの読書状態更新リクエストのDTO
 *
 * @author 02003
 * @componentId C4
 */
public record UpdateUserReadingStateRequest(
        String userId,
        int currentPage,
        String comment) {
}
