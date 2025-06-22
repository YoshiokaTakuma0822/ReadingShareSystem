package com.readingshare.chat.dto;

/**
 * グループチャットの進捗記録リクエストDTO。
 *
 * @author 23001
 * @componentIdName C04 グループチャット
 * @moduleIdName M0410 進捗記録リクエストDTO
 */
public record RecordProgressRequest(
        int currentPage) {
}
