package com.readingshare.room.domain.model;

/**
 * 部屋のユーザープロファイルを表すクラス。
 *
 * @author 02004
 * @componentId C3
 * @moduleName ルームユーザープロファイル
 */
public class RoomUserProfile {

    private final String userId;
    private final String userName;
    private final String iconUrl;

    /**
     * コンストラクタ。
     *
     * @param userId   ユーザーID
     * @param userName ユーザー名
     * @param iconUrl  ユーザーのアイコンURL
     */
    public RoomUserProfile(String userId, String userName, String iconUrl) {
        this.userId = userId;
        this.userName = userName;
        this.iconUrl = iconUrl;
    }

    /**
     * ユーザーIDを取得します。
     *
     * @return ユーザーID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * ユーザー名を取得します。
     *
     * @return ユーザー名
     */
    public String getUserName() {
        return userName;
    }

    /**
     * ユーザーのアイコンURLを取得します。
     *
     * @return アイコンURL
     */
    public String getIconUrl() {
        return iconUrl;
    }
}
