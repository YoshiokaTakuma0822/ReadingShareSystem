package com.readingshare.survey.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * アンケート回答エンティティ。
 */
@Entity
@Table(name = "survey_answers")
public class SurveyAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 回答ID (主キー)

    @NotNull
    @Column(name = "survey_id", nullable = false)
    private Long surveyId; // 関連するアンケートID

    @NotNull
    @Column(name = "responder_user_id", nullable = false)
    private Long responderUserId; // 回答ユーザーID

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "survey_answer_details", joinColumns = @JoinColumn(name = "survey_answer_id"))
    @MapKeyColumn(name = "question_identifier") // 質問を識別するためのキー (例: question_text, question_uuidなど)
    @Column(name = "answer_text", columnDefinition = "TEXT")
    private Map<String, String> answers = new HashMap<>(); // 回答内容 (質問識別子 -> 回答テキスト)

    @NotNull
    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt; // 回答提出日時

    // デフォルトコンストラクタ
    public SurveyAnswer() {}

    public SurveyAnswer(Long id, Long surveyId, Long responderUserId, Map<String, String> answers, Instant submittedAt) {
        this.id = id;
        this.surveyId = surveyId;
        this.responderUserId = responderUserId;
        this.answers = answers;
        this.submittedAt = submittedAt;
    }

    // Getters
    public Long getId() { return id; }
    public Long getSurveyId() { return surveyId; }
    public Long getResponderUserId() { return responderUserId; }
    public Map<String, String> getAnswers() { return answers; }
    public Instant getSubmittedAt() { return submittedAt; }

    // Setters (JPAのためにidは必要)
    public void setId(Long id) { this.id = id; }
    public void setSurveyId(Long surveyId) { this.surveyId = surveyId; }
    public void setResponderUserId(Long responderUserId) { this.responderUserId = responderUserId; }
    public void setAnswers(Map<String, String> answers) { this.answers = answers; }
    public void setSubmittedAt(Instant submittedAt) { this.submittedAt = submittedAt; }
}