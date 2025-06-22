package com.readingshare.state.dto;

import java.util.List;

/**
 * 部屋の読書状態レスポンスのDTO
 *
 * @author 02003
 * @componentId C4
 */
public record RoomReadingStateResponse(
        String roomId,
        List<UserReadingStateResponse> userStates) {
}
