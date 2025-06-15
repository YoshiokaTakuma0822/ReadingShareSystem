package com.readingshare.survey.domain.model;

import java.time.LocalDateTime;
import java.util.Map;
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
 * アンケートへの回答を表すエンティティ。
 * 要求仕様書「(4)アンケートに回答する」に対応。
 */
@Entity
@Table(name = "survey_answers")
public class SurveyAnswer {
    private static final Logger logger = LoggerFactory.getLogger(SurveyAnswer.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "survey_id", columnDefinition = "UUID")
    private UUID surveyId;

    @Column(name = "user_id", columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "answers", columnDefinition = "jsonb")
    private String answersJson;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    public SurveyAnswer() {
    }

    public SurveyAnswer(UUID surveyId, UUID userId, Map<Integer, Integer> answers) {
        if (surveyId == null || userId == null || answers == null || answers.isEmpty()) {
            throw new IllegalArgumentException("Survey ID, User ID, and answers cannot be null or empty.");
        }
        this.id = UUID.randomUUID();
        this.surveyId = surveyId;
        this.userId = userId;
        setAnswers(answers);
        this.answeredAt = LocalDateTime.now();
    }

    public void setAnswers(Map<Integer, Integer> answers) {
        try {
            this.answersJson = objectMapper.writeValueAsString(answers);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize answers to JSON", e);
            throw new IllegalArgumentException("Failed to save answers", e);
        }
    }

    public Map<Integer, Integer> getAnswers() {
        try {
            return objectMapper.readValue(answersJson, new TypeReference<Map<Integer, Integer>>() {
            });
        } catch (JsonProcessingException e) {
            logger.error("Failed to deserialize answers from JSON", e);
            throw new IllegalStateException("Failed to read answers", e);
        }
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public UUID getSurveyId() {
        return surveyId;
    }

    public UUID getUserId() {
        return userId;
    }

    public LocalDateTime getAnsweredAt() {
        return answeredAt;
    }
}
