package com.readingshare.survey.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

// W8 アンケート回答画面からのリクエストデータ
public record SubmitSurveyAnswerRequest(
        UUID userId,
        Map<String, List<String>> answers, // Map<questionText, selectedOptionTexts> - 文字列ベース
        boolean isAnonymous, // 匿名回答かどうか
        Map<String, List<String>> addedOptions // 追加選択肢（questionTextごと）
) {
    // デフォルトコンストラクタ（下位互換性のため）
    public SubmitSurveyAnswerRequest(UUID userId, Map<String, List<String>> answers) {
        this(userId, answers, false, null);
    }
}
