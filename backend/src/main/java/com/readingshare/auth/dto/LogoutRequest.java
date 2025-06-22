package com.readingshare.auth.dto;

/**
 * ログアウトリクエストDTO。
 *
 * @author 003
 * @componentIdName C02 ログイン・会員登録
 * @moduleIdName M0217 ログアウトリクエストDTO
 */
public record LogoutRequest() {
    // ログアウトはトークンベースで行うため、リクエストボディは不要
    // Authorizationヘッダーからトークンを取得する
}
