package com.readingshare.room.service.dto;

/**
 * 部屋参加APIのリクエストDTO
 */
public class JoinRoomRequest {

    private String roomId;
    private String userId;

    public JoinRoomRequest() {
        // デフォルトコンストラクタ
    }

    public JoinRoomRequest(String roomId, String userId) {
        this.roomId = roomId;
        this.userId = userId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "JoinRoomRequest{" +
                "roomId='" + roomId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}