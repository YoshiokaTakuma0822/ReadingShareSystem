package com.readingshare.survey.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// W9 アンケート結果画面へのレスポンスデータ
public record SurveyResultResponse(
        UUID surveyId,
        String title,
        int totalRespondents,
        List<QuestionResultResponse> results,
        OffsetDateTime endTime,
        boolean isExpired) {
    public record QuestionResultResponse(
            String questionText,
            // Map<選択肢のテキスト, 票数>
            Map<String, Long> votes) {
    }
}
