package com.readingshare.survey.service.dto;

public record SubmitSurveyAnswerRequest(
        String surveyId,
        String userId,
        String answer) {
}
