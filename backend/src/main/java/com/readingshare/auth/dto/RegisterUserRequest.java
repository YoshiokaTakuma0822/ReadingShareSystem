package com.readingshare.auth.dto;

/**
 * 会員登録リクエストのDTO。
 *
 * @author 003
 * @componentIdName C02 ログイン・会員登録
 * @moduleIdName M0218 会員登録リクエストDTO
 */
public record RegisterUserRequest(
        String username,
        String password) {
}
