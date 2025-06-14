package com.readingshare.survey.service.dto;

import java.util.Map;

/**
 * アンケート回答提出リクエストのDTO。
 */
public class SubmitSurveyAnswerRequest {
    // key: 質問ID (Stringとして扱うか、適切な型に変換), value: 回答テキスト
    private Map<String, String> answers;

    // Getters and Setters
    public Map<String, String> getAnswers() { return answers; }
    public void setAnswers(Map<String, String> answers) { this.answers = answers; }
}