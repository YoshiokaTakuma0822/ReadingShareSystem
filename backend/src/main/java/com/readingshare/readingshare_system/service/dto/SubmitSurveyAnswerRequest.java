package com.readingshare.readingshare_system.service.dto;

public class SubmitSurveyAnswerRequest {
    private String surveyId;
    private String userId;
    private String answer;

    public SubmitSurveyAnswerRequest(String surveyId, String userId, String answer) {
        this.surveyId = surveyId;
        this.userId = userId;
        this.answer = answer;
    }

    public String getSurveyId() {
        return surveyId;
    }
}
