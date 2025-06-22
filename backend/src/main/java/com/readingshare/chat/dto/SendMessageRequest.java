package com.readingshare.chat.dto;

/**
 * メッセージ送信リクエストのDTO。
 */
public record SendMessageRequest(
        String messageContent) {
}
