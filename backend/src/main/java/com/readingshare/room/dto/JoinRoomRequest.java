package com.readingshare.room.dto;

import java.util.UUID;

/**
 * 部屋参加リクエストのDTO。
 *
 * @author 23004
 */
public record JoinRoomRequest(
        UUID roomId,
        UUID userId,
        String roomPassword // パスワード保護された部屋の場合に必要
) {
}
