package com.readingshare.room.dto;

import java.util.UUID;

/**
 * 部屋情報更新APIのリクエストDTO
 */
public record UpdateRoomRequest(
        Integer totalPages // 追加: 本のページ数
) {
}
