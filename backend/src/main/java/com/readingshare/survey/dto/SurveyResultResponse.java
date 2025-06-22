package com.readingshare.survey.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * SurveyResultResponse は、アンケート結果を表すDTOクラスです。
 * アンケートID、タイトル、回答者数、質問結果を含みます。
 */
public record SurveyResultResponse(
        UUID surveyId,
        String title,
        int totalRespondents,
        List<QuestionResultResponse> results) {
    /**
     * 質問結果を取得します。
     *
     * @return 質問結果のリスト
     */
    public record QuestionResultResponse(
            String questionText,
            // Map<選択肢のテキスト, 票数>
            Map<String, Long> votes) {
    }
}
