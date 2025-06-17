package com.readingshare.survey.dto;

import java.util.Map;
import java.util.UUID;

// W8 アンケート回答画面からのリクエストデータ
public record SubmitSurveyAnswerRequest(
                UUID userId,
                Map<String, Integer> answers // Map<questionText, selectedOptionIndex>
) {
}
