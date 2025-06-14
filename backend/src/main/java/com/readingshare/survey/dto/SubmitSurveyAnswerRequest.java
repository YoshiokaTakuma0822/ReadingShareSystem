package com.readingshare.survey.dto;

import java.util.Map;

// W8 アンケート回答画面からのリクエストデータ
public record SubmitSurveyAnswerRequest(
    String userId,
    Map<Integer, Integer> answers // Map<questionIndex, selectedOptionIndex>
) {}
