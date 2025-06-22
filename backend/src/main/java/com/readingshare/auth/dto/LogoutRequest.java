package com.readingshare.auth.dto;

/**
 * ログアウトリクエストDTO。
 *
 * @author 003
 * @componentId C2
 * @moduleName ログアウトリクエストDTO
 */
public record LogoutRequest() {
    // ログアウトはトークンベースで行うため、リクエストボディは不要
    // Authorizationヘッダーからトークンを取得する
}
