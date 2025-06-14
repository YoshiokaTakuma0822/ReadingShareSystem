package com.readingshare.survey.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

/**
 * アンケートの質問エンティティ。
 * Surveyエンティティの子エンティティとして管理される。
 */
@Entity
@Table(name = "survey_questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 質問ID (主キー)

    // 親のSurveyIDは@JoinColumnで設定されるため、ここでは直接定義しない

    @NotNull
    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText; // 質問内容

    @Column(name = "question_type")
    private String questionType; // 質問の種類 (例: "TEXT", "SINGLE_CHOICE", "MULTIPLE_CHOICE")

    @Column(name = "options", columnDefinition = "TEXT") // 選択肢 (カンマ区切りJSON形式など)
    private String options;

    // デフォルトコンストラクタ
    public Question() {}

    public Question(Long id, String questionText, String questionType, String options) {
        this.id = id;
        this.questionText = questionText;
        this.questionType = questionType;
        this.options = options;
    }

    // Getters
    public Long getId() { return id; }
    public String getQuestionText() { return questionText; }
    public String getQuestionType() { return questionType; }
    public String getOptions() { return options; }

    // Setters (JPAのためにidは必要)
    public void setId(Long id) { this.id = id; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }
    public void setOptions(String options) { this.options = options; }
}