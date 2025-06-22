package com.readingshare.survey.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * SubmitSurveyAnswerRequestは、アンケート回答送信リクエストのDTOです。
 *
 * @author 02002
 * @componentId C4
 * @moduleName アンケート回答送信リクエストDTO
 */
// W8 アンケート回答画面からのリクエストデータ
public record SubmitSurveyAnswerRequest(
        UUID userId,
        Map<String, List<String>> answers, // Map<questionText, selectedOptionTexts> - 文字列ベース
        boolean isAnonymous // 匿名回答かどうか
) {
    // デフォルトコンストラクタ（下位互換性のため）
    public SubmitSurveyAnswerRequest(UUID userId, Map<String, List<String>> answers) {
        this(userId, answers, false);
    }

    /**
     * 回答者IDを取得します。
     *
     * @return 回答者ID
     */
    public UUID userId() {
        return userId;
    }

    /**
     * 回答内容を取得します。
     *
     * @return 回答内容のマップ
     */
    public Map<String, List<String>> answers() {
        return answers;
    }

    /**
     * 匿名フラグを取得します。
     *
     * @return 匿名回答の場合はtrue、それ以外はfalse
     */
    public boolean isAnonymous() {
        return isAnonymous;
    }
}
