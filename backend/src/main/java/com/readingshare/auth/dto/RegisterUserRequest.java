package com.readingshare.auth.dto;

/**
 * 会員登録リクエストのDTO。
 *
 * @author 02005
 * @componentId C2
 * @moduleName 会員登録リクエストDTO
 */
public record RegisterUserRequest(
        String username,
        String password) {
}
