package com.readingshare.auth.dto;

/**
 * ログインリクエストのDTO。
 */
public record LoginRequest(
        String username,
        String password) {
}
