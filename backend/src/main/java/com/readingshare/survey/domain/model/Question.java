package com.readingshare.survey.domain.model;

import java.util.List;

/**
 * 質問エンティティ。
 * JSON形式でシリアライズされてデータベースに保存される。
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

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public boolean isAllowAnonymous() {
        return allowAnonymous;
    }

    public boolean isAllowAddOptions() {
        return allowAddOptions;
    }

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
