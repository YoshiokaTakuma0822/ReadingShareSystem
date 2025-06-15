package com.readingshare.survey.domain.model;

import java.util.List;

/**
 * 質問エンティティ。
 */
public class Question {
    private final String questionText;
    private final List<String> options; // 選択肢のリスト

    public Question(String questionText, List<String> options) {
        if (questionText == null || questionText.isBlank()) {
            throw new IllegalArgumentException("Question text cannot be null or empty.");
        }
        if (options == null || options.size() < 2) {
            // 選択肢は少なくとも2つ必要
            throw new IllegalArgumentException("A question must have at least two options.");
        }
        this.questionText = questionText;
        this.options = options;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getOptions() {
        return options;
    }
}
