package com.readingshare.survey.dto;

import java.util.List;
import java.util.UUID;

// W7 アンケート作成画面からのリクエストデータ
public record CreateSurveyRequest(
        UUID roomId,
        String title,
        List<QuestionDto> questions) {
    public record QuestionDto(String questionText, List<String> options) {
    }
}
