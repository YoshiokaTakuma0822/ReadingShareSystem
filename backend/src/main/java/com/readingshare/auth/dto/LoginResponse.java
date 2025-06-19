package com.readingshare.auth.dto;

import java.util.UUID;

/**
 * ログイン成功時のレスポンスDTO。
 *
 * @param userId   ユーザーID
 * @param token    Bearer Token
 * @param username ユーザー名
 */
public record LoginResponse(
        UUID userId,
        String token,
        String username) {
}
