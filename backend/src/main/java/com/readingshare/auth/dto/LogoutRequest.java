package com.readingshare.auth.dto;

/**
 * ログアウトリクエストDTO。
 */
public record LogoutRequest() {
    // ログアウトはトークンベースで行うため、リクエストボディは不要
    // Authorizationヘッダーからトークンを取得する
}
