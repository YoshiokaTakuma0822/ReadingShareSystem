package com.readingshare.room.domain.service;

import com.readingshare.room.domain.model.RoomUserProfile;

/**
 * 部屋のユーザープロファイル取得サービスインターフェース。
 * ユーザープロファイルの管理や、Room参加・退出などの処理を行う。
 *
 * @author 02004
 * @componentId C3
 * @moduleName ルームユーザーサービス
 */
public interface RoomUserService {
    /**
     * 部屋IDとユーザIDからユーザプロフィールを取得
     */
    RoomUserProfile getUserProfile(String roomId, String userId);
}
