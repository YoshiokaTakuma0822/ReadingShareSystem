package com.readingshare.auth.dto;

/**
 * ログインリクエストのDTO。
 *
 * @author 02005
 * @componentId C2
 * @moduleName ログインリクエストDTO
 */
public record LoginRequest(
        String username,
        String password) {
}
