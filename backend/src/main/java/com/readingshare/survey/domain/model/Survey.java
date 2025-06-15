package com.readingshare.survey.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * アンケート集約のルートエンティティ。
 * 要求仕様書「(3)アンケートを作成する」に対応。
 */
@Entity
@Table(name = "surveys")
public class Survey {
    private static final Logger logger = LoggerFactory.getLogger(Survey.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "room_id")
    private UUID roomId; // アンケートが実施される部屋のID

    @Column(name = "title")
    private String title; // アンケートのタイトル

    @Column(name = "questions", columnDefinition = "jsonb")
    private String questionsJson; // 質問のリスト（JSONB形式）

    @Column(name = "created_at")
    private LocalDateTime createdAt; // アンケート作成日時

    public Survey() {
    }

    public Survey(UUID roomId, String title, List<Question> questions) {
        if (roomId == null) {
            throw new IllegalArgumentException("Room ID cannot be null.");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or empty.");
        }
        if (questions == null || questions.isEmpty()) {
            throw new IllegalArgumentException("Questions cannot be null or empty.");
        }
        this.id = UUID.randomUUID();
        this.roomId = roomId;
        this.title = title;
        setQuestions(questions);
        this.createdAt = LocalDateTime.now();
    }

    public void setQuestions(List<Question> questions) {
        try {
            this.questionsJson = objectMapper.writeValueAsString(questions);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize questions to JSON", e);
            throw new IllegalArgumentException("Failed to save questions", e);
        }
    }

    public List<Question> getQuestions() {
        try {
            return objectMapper.readValue(questionsJson, new TypeReference<List<Question>>() {
            });
        } catch (JsonProcessingException e) {
            logger.error("Failed to deserialize questions from JSON", e);
            throw new IllegalStateException("Failed to read questions", e);
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getRoomId() {
        return roomId;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
