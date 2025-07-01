package com.readingshare.chat.dto;

public class ChatMessageDto {
    private String roomId;
    private String senderId;
    private String senderName;
    private String content;
    private String sentAt;

    public ChatMessageDto() {
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    @Override
    public String toString() {
        return "ChatMessageDto{" +
                "roomId='" + roomId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", senderName='" + senderName + '\'' +
                ", content='" + content + '\'' +
                ", sentAt='" + sentAt + '\'' +
                '}';
    }
}
