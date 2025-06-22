package com.readingshare.survey.dto;

import java.util.List;
import java.util.UUID;

import com.readingshare.survey.domain.model.QuestionType;

/**
 * CreateSurveyRequest は、アンケート作成リクエストを表すDTOクラスです。
 * 部屋ID、タイトル、質問リストを含みます。
 */
// W7 アンケート作成画面からのリクエストデータ
public record CreateSurveyRequest(
        UUID roomId,
        String title,
        List<QuestionDto> questions) {
    /**
     * 質問リストを取得します。
     *
     * @return 質問リスト
     */
    public List<QuestionDto> questions() {
        return questions;
    }

    public record QuestionDto(
            String questionText,
            List<String> options,
            QuestionType questionType, // 質問タイプを追加
            boolean allowAnonymous, // 匿名投票を許可
            boolean allowAddOptions // 選択肢の追加を許可
    ) {
        // デフォルトコンストラクタ（下位互換性のため）
        public QuestionDto(String questionText, List<String> options) {
            this(questionText, options, QuestionType.SINGLE_CHOICE, false, false);
        }

        // 質問タイプのみ指定するコンストラクタ
        public QuestionDto(String questionText, List<String> options, QuestionType questionType) {
            this(questionText, options, questionType, false, false);
        }
    }
}
