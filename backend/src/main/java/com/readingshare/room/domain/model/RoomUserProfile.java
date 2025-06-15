package com.readingshare.room.domain.model;

public class RoomUserProfile {
    private final String userId;
    private final String userName;
    private final String iconUrl;

    public RoomUserProfile(String userId, String userName, String iconUrl) {
        this.userId = userId;
        this.userName = userName;
        this.iconUrl = iconUrl;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getIconUrl() {
        return iconUrl;
    }
}
