package com.readingshare.auth.dto;

/**
 * ログインリクエストのDTO。
 *
 * @author 003
 * @componentIdName C02 ログイン・会員登録
 * @moduleIdName M0215 ログインリクエストDTO
 */
public record LoginRequest(
        String username,
        String password) {
}
