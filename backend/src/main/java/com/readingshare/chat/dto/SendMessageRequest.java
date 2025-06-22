package com.readingshare.chat.dto;

/**
 * グループチャットのメッセージ送信リクエストDTO。
 *
 * @author 23001
 * @componentIdName C04 グループチャット
 * @moduleIdName M0411 メッセージ送信リクエストDTO
 */
public record SendMessageRequest(
        String messageContent) {
}
