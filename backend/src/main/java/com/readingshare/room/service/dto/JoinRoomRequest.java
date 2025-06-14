package com.readingshare.room.service.dto;

/**
 * 部屋参加リクエストのDTO。
 */
public class JoinRoomRequest {
    private String roomPassword; // パスワード保護された部屋の場合に必要

    // Getters and Setters
    public String getRoomPassword() { return roomPassword; }
    public void setRoomPassword(String roomPassword) { this.roomPassword = roomPassword; }
}