package com.readingshare.survey.domain.model;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * アンケートへの回答を表すエンティティ。
 * 要求仕様書「(4)アンケートに回答する」に対応。
 */
@Getter
public class SurveyAnswer {

    private final SurveyId surveyId;
    private final String userId; // 回答者のユーザーID
    // キー: 質問のインデックス(0-origin), 値: 選択された選択肢のインデックス(0-origin)
    private final Map<Integer, Integer> answers;
    private final LocalDateTime answeredAt;

    public SurveyAnswer(SurveyId surveyId, String userId, Map<Integer, Integer> answers) {
        if (surveyId == null || userId == null || answers == null || answers.isEmpty()) {
            throw new IllegalArgumentException("Survey ID, User ID, and answers cannot be null or empty.");
        }
        this.surveyId = surveyId;
        this.userId = userId;
        this.answers = answers;
        this.answeredAt = LocalDateTime.now();
    }
}
