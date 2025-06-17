package com.readingshare.survey.domain.model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "survey_id", columnDefinition = "UUID")
    private UUID surveyId;

    @Column(name = "user_id", columnDefinition = "UUID")
    private UUID userId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "answers", columnDefinition = "jsonb")
    private Map<Integer, Integer> answers;

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
        this.answers = answers;
        this.answeredAt = LocalDateTime.now();
    }

    public void setAnswers(Map<Integer, Integer> answers) {
        this.answers = answers;
    }

    public Map<Integer, Integer> getAnswers() {
        return answers;
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
