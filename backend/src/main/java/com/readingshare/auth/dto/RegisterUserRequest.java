package com.readingshare.auth.dto;

/**
 * 会員登録リクエストのDTO。
 */
public record RegisterUserRequest(
        String username,
        String password) {
}
