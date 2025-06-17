package com.readingshare.survey.domain.model;

import java.time.LocalDateTime;
import java.util.List;
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
    private Map<String, List<String>> answers; // 文字列ベース

    @Column(name = "is_anonymous")
    private boolean isAnonymous; // 匿名回答かどうか

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    public SurveyAnswer() {
    }

    public SurveyAnswer(UUID surveyId, UUID userId, Map<String, List<String>> answers) {
        this(surveyId, userId, answers, false); // デフォルトは非匿名
    }

    public SurveyAnswer(UUID surveyId, UUID userId, Map<String, List<String>> answers, boolean isAnonymous) {
        if (surveyId == null || userId == null || answers == null || answers.isEmpty()) {
            throw new IllegalArgumentException("Survey ID, User ID, and answers cannot be null or empty.");
        }
        this.id = UUID.randomUUID();
        this.surveyId = surveyId;
        this.userId = userId;
        this.answers = answers;
        this.isAnonymous = isAnonymous;
        this.answeredAt = LocalDateTime.now();
    }

    public void setAnswers(Map<String, List<String>> answers) {
        this.answers = answers;
    }

    public Map<String, List<String>> getAnswers() {
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

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }
}
