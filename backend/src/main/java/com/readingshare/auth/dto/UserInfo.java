package com.readingshare.auth.dto;

import java.util.UUID;

/**
 * ユーザー情報のレスポンスDTO。
 *
 * @author 02005
 * @componentId C2
 * @moduleName ユーザー情報DTO
 */
public record UserInfo(UUID userId, String username) {
}
