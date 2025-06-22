package com.readingshare.chat.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.readingshare.chat.domain.model.ChatMessage;
import com.readingshare.survey.domain.model.Survey;

/**
 * グループチャットのストリームアイテムDTO。
 *
 * @author 02001
 * @componentId C4
 * @moduleName ストリームアイテムDTO
 */
public class ChatStreamItemDto {
    /**
     * チャットストリームアイテムの種類を表す列挙型。
     */
    public enum ItemType {
        MESSAGE, SURVEY;

        /**
         * アイテムの種類をJSON形式の文字列として返します。
         *
         * @return アイテムの種類を表す小文字の文字列
         */
        @JsonValue
        public String toJson() {
            return this.name().toLowerCase();
        }
    }

    private ItemType type;
    private ChatMessage message;
    private Survey survey;
    private Instant sentAt;
    private LocalDateTime createdAt;

    /**
     * チャットメッセージを基にChatStreamItemDtoを作成します。
     *
     * @param message チャットメッセージ
     */
    public ChatStreamItemDto(ChatMessage message) {
        this.type = ItemType.MESSAGE;
        this.message = message;
        this.survey = null;
        this.sentAt = message.getSentAt();
        this.createdAt = null;
    }

    /**
     * アンケートを基にChatStreamItemDtoを作成します。
     *
     * @param survey アンケート
     */
    public ChatStreamItemDto(Survey survey) {
        this.type = ItemType.SURVEY;
        this.message = null;
        this.survey = survey;
        this.sentAt = null;
        this.createdAt = survey.getCreatedAt();
    }

    /**
     * アイテムの種類を文字列として取得します。
     *
     * @return アイテムの種類を表す文字列
     */
    @JsonProperty("type")
    public String getTypeString() {
        return type.toJson();
    }

    /**
     * チャットメッセージを取得します。
     *
     * @return チャットメッセージ
     */
    @JsonProperty("message")
    public ChatMessage getMessage() {
        return message;
    }

    /**
     * アンケートを取得します。
     *
     * @return アンケート
     */
    @JsonProperty("survey")
    public Survey getSurvey() {
        return survey;
    }

    /**
     * 作成日時を文字列として取得します。
     *
     * @return 作成日時を表す文字列
     */
    @JsonGetter("createdAt")
    public String getCreatedAtString() {
        if (type == ItemType.MESSAGE && sentAt != null) {
            return sentAt.toString();
        } else if (type == ItemType.SURVEY && createdAt != null) {
            return createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        return null;
    }

    /**
     * アイテムの種類を取得します。
     *
     * @return アイテムの種類
     */
    public ItemType getType() {
        return type;
    }

    /**
     * 送信日時を取得します。
     *
     * @return 送信日時
     */
    public Instant getSentAt() {
        return sentAt;
    }

    /**
     * 作成日時を取得します。
     *
     * @return 作成日時
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
