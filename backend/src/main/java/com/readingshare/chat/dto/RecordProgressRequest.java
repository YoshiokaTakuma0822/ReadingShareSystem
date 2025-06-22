package com.readingshare.chat.dto;

/**
 * グループチャットの進捗記録リクエストDTO。
 *
 * @author 02001
 * @componentId C4
 * @moduleName 進捗記録リクエストDTO
 */
public record RecordProgressRequest(
        int currentPage) {
}
