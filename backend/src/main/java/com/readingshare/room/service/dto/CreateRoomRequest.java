package com.readingshare.room.service.dto;

/**
 * 部屋作成APIのリクエストDTO
 */
public class CreateRoomRequest {

    private String roomName;
    private Long hostUserId;

    public CreateRoomRequest() {
        // デフォルトコンストラクタ
    }

    public CreateRoomRequest(String roomName, Long hostUserId) {
        this.roomName = roomName;
        this.hostUserId = hostUserId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Long getHostUserId() {
        return hostUserId;
    }

    public void setHostUserId(Long hostUserId) {
        this.hostUserId = hostUserId;
    }

    @Override
    public String toString() {
        return "CreateRoomRequest{" +
                "roomName='" + roomName + '\'' +
                ", hostUserId=" + hostUserId +
                '}';
    }
}
