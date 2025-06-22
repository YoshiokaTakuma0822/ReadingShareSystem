package com.readingshare.survey.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * SurveyResultResponseは、アンケート結果レスポンスのDTOです。
 *
 * @author 02002
 * @componentId C4
 * @moduleName アンケート結果レスポンスDTO
 * @see QuestionResultResponse
 */
public record SurveyResultResponse(
        UUID surveyId,
        String title,
        int totalRespondents,
        List<QuestionResultResponse> results) {
    /**
     * 質問結果を取得します。
     *
     * @author 02002
     * @componentId C4
     * @moduleName 質問結果レスポンスDTO
     */
    public record QuestionResultResponse(
            String questionText,
            // Map<選択肢のテキスト, 票数>
            Map<String, Long> votes) {
    }
}
