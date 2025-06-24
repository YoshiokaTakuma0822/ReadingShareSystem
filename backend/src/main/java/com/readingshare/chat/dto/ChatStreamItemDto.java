package com.readingshare.chat.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.readingshare.chat.domain.model.ChatMessage;
import com.readingshare.survey.domain.model.Survey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatStreamItemDto {
    public enum ItemType { MESSAGE, SURVEY;
        @JsonValue
        public String toJson() { return this.name().toLowerCase(); }
    }

    private ItemType type;
    private ChatMessage message;
    private Survey survey;
    private Instant sentAt;
    private LocalDateTime createdAt;

    public ChatStreamItemDto(ChatMessage message) {
        this.type = ItemType.MESSAGE;
        this.message = message;
        this.survey = null;
        this.sentAt = message.getSentAt();
        this.createdAt = null;
    }

    public ChatStreamItemDto(Survey survey) {
        this.type = ItemType.SURVEY;
        this.message = null;
        this.survey = survey;
        this.sentAt = null;
        this.createdAt = survey.getCreatedAt();
    }

    @JsonProperty("type")
    public String getTypeString() { return type.toJson(); }
    @JsonProperty("message")
    public ChatMessage getMessage() { return message; }
    @JsonProperty("survey")
    public Survey getSurvey() { return survey; }
    @JsonGetter("createdAt")
    public String getCreatedAtString() {
        if (type == ItemType.MESSAGE && sentAt != null) {
            return sentAt.toString();
        } else if (type == ItemType.SURVEY && createdAt != null) {
            return createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        return null;
    }

    public ItemType getType() { return type; }
    public Instant getSentAt() { return sentAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
