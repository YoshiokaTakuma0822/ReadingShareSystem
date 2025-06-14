package com.readingshare.state.domain.model;

import java.util.Objects;

public class UserReadingState {
    private final String userId;
    private final String userName;
    private final String iconUrl;
    private final int currentPage;
    private final String comment;
    private final int autoTurnIntervalSec; // 自動めくり間隔（秒）

    public UserReadingState(String userId, String userName, String iconUrl, int currentPage, String comment, int autoTurnIntervalSec) {
        this.userId = userId;
        this.userName = userName;
        this.iconUrl = iconUrl;
        this.currentPage = currentPage;
        this.comment = comment;
        this.autoTurnIntervalSec = autoTurnIntervalSec;
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

    public int getCurrentPage() {
        return currentPage;
    }

    public String getComment() {
        return comment;
    }

    public int getAutoTurnIntervalSec() {
        return autoTurnIntervalSec;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserReadingState that = (UserReadingState) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
