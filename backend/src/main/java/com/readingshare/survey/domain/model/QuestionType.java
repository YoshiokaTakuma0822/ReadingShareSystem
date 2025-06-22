package com.readingshare.survey.domain.model;

/**
 * QuestionTypeは、アンケートの質問タイプを表す列挙型です。
 *
 * @author 02002
 * @componentId C4
 * @moduleName アンケート質問タイプ
 */
public enum QuestionType {
    SINGLE_CHOICE, // 単一選択（ラジオボタン）
    MULTIPLE_CHOICE // 複数選択（チェックボックス）
}
