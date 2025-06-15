package com.readingshare.room.domain.service;

import com.readingshare.room.domain.model.RoomUserProfile;

public interface RoomUserService {
    /**
     * 部屋IDとユーザIDからユーザプロフィールを取得
     */
    RoomUserProfile getUserProfile(String roomId, String userId);
}
