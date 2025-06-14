package com.readingshare.chat.service.dto;

/**
 * メッセージ送信リクエストのDTO。
 */
public class SendMessageRequest {
    private String messageContent;

    // Getters and Setters
    public String getMessageContent() { return messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }
}