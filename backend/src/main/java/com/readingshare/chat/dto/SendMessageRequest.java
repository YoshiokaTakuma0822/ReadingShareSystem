package com.readingshare.chat.dto;

/**
 * グループチャットのメッセージ送信リクエストDTO。
 *
 * @author 02001
 * @componentId C4
 * @moduleName メッセージ送信リクエストDTO
 */
public record SendMessageRequest(
        String messageContent) {
}
