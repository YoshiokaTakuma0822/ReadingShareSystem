package com.readingshare.room.service.dto;

/**
 * 部屋作成リクエストのDTO。
 */
public class CreateRoomRequest {
    private String roomName;
    private String bookTitle;
    private String roomPassword; // オプション

    // Getters and Setters
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public String getRoomPassword() { return roomPassword; }
    public void setRoomPassword(String roomPassword) { this.roomPassword = roomPassword; }
}