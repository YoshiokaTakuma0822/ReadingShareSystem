package com.readingshare.survey.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;

// W8 アンケート回答画面からのリクエストデータ
public record SubmitSurveyAnswerRequest(
        UUID userId,
        @JsonAlias({"answers"}) Map<String, List<String>> answers, // Mapまたはobject
        boolean isAnonymous // 匿名回答かどうか
) {
    // デフォルトコンストラクタ（下位互換性のため）
    public SubmitSurveyAnswerRequest(UUID userId, Map<String, List<String>> answers) {
        this(userId, answers, false);
    }
}
