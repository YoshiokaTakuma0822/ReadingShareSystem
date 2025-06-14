package com.readingshare.room.service.dto;

/**
 * 部屋参加リクエストのDTO。
 */
public class JoinRoomRequest {
    private String roomPassword; // パスワード保護された部屋の場合に必要

    private Long roomId;
    private Long userId;

    public JoinRoomRequest() {
    }

    public JoinRoomRequest(Long roomId, Long userId) {
        this.roomId = roomId;
        this.userId = userId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "JoinRoomRequest{" +
                "roomId=" + roomId +
                ", userId=" + userId +
                '}';
    }
}
