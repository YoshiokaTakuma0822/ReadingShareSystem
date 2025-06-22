package com.readingshare.chat.dto;

import java.time.Instant;

/**
 * メッセージ送信リクエストのDTO。
 */
public record SendMessageRequest(
        String messageContent,
        Instant sentAt // 送信時刻を追加
) {
}
