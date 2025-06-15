package com.readingshare.survey.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.JoinColumn;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * アンケートへの回答を表すエンティティ。
 * 要求仕様書「(4)アンケートに回答する」に対応。
 */
@Entity
@Table(name = "survey_answers")
public class SurveyAnswer {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "survey_id")
    private String surveyId;

    @Column(name = "user_id")
    private String userId; // 回答者のユーザーID

    @ElementCollection
    @CollectionTable(name = "survey_answer_details", joinColumns = @JoinColumn(name = "answer_id"))
    @MapKeyColumn(name = "question_index")
    @Column(name = "selected_option_index")
    private Map<Integer, Integer> answers;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    public SurveyAnswer() {}

    public SurveyAnswer(SurveyId surveyId, String userId, Map<Integer, Integer> answers) {
        if (surveyId == null || userId == null || answers == null || answers.isEmpty()) {
            throw new IllegalArgumentException("Survey ID, User ID, and answers cannot be null or empty.");
        }
        this.surveyId = surveyId.getValue();
        this.userId = userId;
        this.answers = answers;
        this.answeredAt = LocalDateTime.now();
    }

    public String getSurveyId() {
        return surveyId;
    }

    public String getUserId() {
        return userId;
    }

    public Map<Integer, Integer> getAnswers() {
        return answers;
    }

    public LocalDateTime getAnsweredAt() {
        return answeredAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
