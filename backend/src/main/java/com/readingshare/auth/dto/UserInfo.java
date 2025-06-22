package com.readingshare.auth.dto;

import java.util.UUID;

/**
 * ユーザー情報のレスポンスDTO。
 *
 * @author 003
 * @componentId C2
 * @moduleName ユーザー情報DTO
 */
public record UserInfo(UUID userId, String username) {
}
