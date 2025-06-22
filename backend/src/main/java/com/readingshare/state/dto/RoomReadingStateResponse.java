package com.readingshare.state.dto;

import java.util.List;

/**
 * 部屋の読書状態レスポンスのDTO
 *
 * @author 02003
 * @componentId C4
 * @moduleName 部屋読書状態レスポンスDTO
 */
public record RoomReadingStateResponse(
        String roomId,
        List<UserReadingStateResponse> userStates) {
}
