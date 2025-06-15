package com.readingshare.room.service.dto;

/**
 * 部屋作成APIのリクエストDTO
 */
public class CreateRoomRequest {

    private String roomName;
    private Long hostUserId;
    private String bookTitle;
    private String password;

    public CreateRoomRequest() {
        // デフォルトコンストラクタ
    }

    public CreateRoomRequest(String roomName, Long hostUserId, String bookTitle, String password) {
        this.roomName = roomName;
        this.hostUserId = hostUserId;
        this.bookTitle = bookTitle;
        this.password = password;
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

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "CreateRoomRequest{" +
                "roomName='" + roomName + '\'' +
                ", hostUserId=" + hostUserId +
                ", bookTitle='" + bookTitle + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
