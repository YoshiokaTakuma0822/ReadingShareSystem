package com.readingshare.survey.service.dto;

import java.util.List;
import java.util.Map;

// W9 アンケート結果画面へのレスポンスデータ
public record SurveyResultDto(
    String surveyId,
    String title,
    int totalRespondents,
    List<QuestionResultDto> results
) {
    public record QuestionResultDto(
        String questionText,
        // Map<選択肢のテキスト, 票数>
        Map<String, Long> votes
    ) {}
}
