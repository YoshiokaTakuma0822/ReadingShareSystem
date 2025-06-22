package com.readingshare.auth.dto;

import java.util.UUID;

/**
 * ログイン成功時のレスポンスDTO。
 *
 * @author 003
 * @componentIdName C02 ログイン・会員登録
 * @moduleIdName M0216 ログインレスポンスDTO
 */
public record LoginResponse(
        UUID userId,
        String token,
        String username) {
}
