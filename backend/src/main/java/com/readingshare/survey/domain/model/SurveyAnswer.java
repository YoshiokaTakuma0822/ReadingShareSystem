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
 * SurveyAnswer は、アンケートへの回答を表すエンティティクラスです。
 * 回答内容、回答者情報、回答日時などを管理します。
 *
 * @author 02002
 * @componentId C4
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

    /**
     * 回答内容を取得します。
     *
     * @return 回答内容のマップ
     */
    public Map<String, List<String>> getAnswers() {
        return answers;
    }

    /**
     * 回答内容を設定します。
     *
     * @param answers 回答内容のマップ
     */
    public void setAnswers(Map<String, List<String>> answers) {
        this.answers = answers;
    }

    // Getters
    /**
     * 回答の一意な識別子を取得します。
     *
     * @return 回答のID
     */
    public UUID getId() {
        return id;
    }

    /**
     * アンケートのIDを取得します。
     *
     * @return アンケートのID
     */
    public UUID getSurveyId() {
        return surveyId;
    }

    /**
     * ユーザーのIDを取得します。
     *
     * @return ユーザーのID
     */
    public UUID getUserId() {
        return userId;
    }

    /**
     * 回答日時を取得します。
     *
     * @return 回答日時
     */
    public LocalDateTime getAnsweredAt() {
        return answeredAt;
    }

    /**
     * 回答が匿名かどうかを取得します。
     *
     * @return 匿名の場合は true、それ以外は false
     */
    public boolean isAnonymous() {
        return isAnonymous;
    }

    /**
     * 回答が匿名かどうかを設定します。
     *
     * @param anonymous 匿名の場合は true、それ以外は false
     */
    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }
}
