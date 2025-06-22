package com.readingshare.auth.dto;

import java.util.UUID;

/**
 * ログイン成功時のレスポンスDTO。
 *
 * @author 02003
 * @componentId C2
 * @moduleName ログインレスポンスDTO
 */
public record LoginResponse(
        UUID userId,
        String token,
        String username) {
}
