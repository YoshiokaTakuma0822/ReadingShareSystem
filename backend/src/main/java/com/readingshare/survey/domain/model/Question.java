package com.readingshare.survey.domain.model;

import java.util.List;

/**
 * Questionは、アンケートの質問を表すクラスです。
 *
 * @author 02002
 * @componentId C4
 * @moduleName アンケート質問モデル
 * @see QuestionType
 */
public class Question {
    private String questionText;

    // @ElementCollection を削除
    private List<String> options; // 選択肢のリスト

    private QuestionType questionType; // 質問タイプ（単一選択 or 複数選択）

    // 新しいオプション機能
    private boolean allowAnonymous; // 匿名投票を許可
    private boolean allowAddOptions; // 選択肢の追加を許可

    public Question() {
    }

    public Question(String questionText, List<String> options) {
        this(questionText, options, QuestionType.SINGLE_CHOICE, false, false); // デフォルト設定
    }

    public Question(String questionText, List<String> options, QuestionType questionType) {
        this(questionText, options, questionType, false, false); // デフォルト設定
    }

    public Question(String questionText, List<String> options, QuestionType questionType,
            boolean allowAnonymous, boolean allowAddOptions) {
        if (questionText == null || questionText.isBlank()) {
            throw new IllegalArgumentException("Question text cannot be null or empty.");
        }
        if (options == null || options.size() < 2) {
            // 選択肢は少なくとも2つ必要
            throw new IllegalArgumentException("A question must have at least two options.");
        }
        if (questionType == null) {
            throw new IllegalArgumentException("Question type cannot be null.");
        }
        this.questionText = questionText;
        this.options = options;
        this.questionType = questionType;
        this.allowAnonymous = allowAnonymous;
        this.allowAddOptions = allowAddOptions;
    }

    /**
     * 質問文を取得します。
     *
     * @return 質問文
     */
    public String getQuestionText() {
        return questionText;
    }

    /**
     * 選択肢のリストを取得します。
     *
     * @return 選択肢のリスト
     */
    public List<String> getOptions() {
        return options;
    }

    /**
     * 質問タイプを取得します。
     *
     * @return 質問タイプ
     */
    public QuestionType getQuestionType() {
        return questionType;
    }

    /**
     * 匿名投票が許可されているかどうかを取得します。
     *
     * @return 匿名投票が許可されている場合はtrue、それ以外の場合はfalse
     */
    public boolean isAllowAnonymous() {
        return allowAnonymous;
    }

    /**
     * 選択肢の追加が許可されているかどうかを取得します。
     *
     * @return 選択肢の追加が許可されている場合はtrue、それ以外の場合はfalse
     */
    public boolean isAllowAddOptions() {
        return allowAddOptions;
    }

    /**
     * 新しい選択肢を追加します。
     *
     * @param newOption 新しい選択肢
     * @throws IllegalStateException    選択肢の追加が許可されていない場合
     * @throws IllegalArgumentException 無効な選択肢が指定された場合
     */
    public void addOption(String newOption) {
        if (!allowAddOptions) {
            throw new IllegalStateException("Adding options is not allowed for this question.");
        }
        if (newOption == null || newOption.isBlank()) {
            throw new IllegalArgumentException("Option cannot be null or empty.");
        }
        if (options.contains(newOption)) {
            throw new IllegalArgumentException("Option already exists.");
        }
        options.add(newOption);
    }
}
