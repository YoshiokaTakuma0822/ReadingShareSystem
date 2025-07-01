package com.readingshare.chat.dto;

public class ChatMessageDto {
    private String roomId;
    private String senderId;
    private String senderName;
    private String content;
    private String sentAt;
    private String messageType; // "TEXT", "SURVEY", "SYSTEM" など
    private String surveyId; // アンケートメッセージの場合のアンケートID

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

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    @Override
    public String toString() {
        return "ChatMessageDto{" +
                "roomId='" + roomId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", senderName='" + senderName + '\'' +
                ", content='" + content + '\'' +
                ", sentAt='" + sentAt + '\'' +
                ", messageType='" + messageType + '\'' +
                ", surveyId='" + surveyId + '\'' +
                '}';
    }
}
