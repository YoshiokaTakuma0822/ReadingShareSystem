package com.readingshare.survey.domain.model;

import java.util.List;
import java.util.UUID;

/**
 * アンケート集約のルートエンティティ。
 * 要求仕様書「(3)アンケートを作成する」に対応。
 */
public class Survey {

    private final SurveyId id;
    private final String roomId; // アンケートが実施される部屋のID
    private final String title; // アンケートのタイトル
    private final List<Question> questions; // 質問のリスト

    public Survey(String roomId, String title, List<Question> questions) {
        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room ID cannot be null or empty.");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or empty.");
        }
        if (questions == null || questions.isEmpty()) {
            throw new IllegalArgumentException("Questions cannot be null or empty.");
        }
        this.id = new SurveyId(UUID.randomUUID().toString());
        this.roomId = roomId;
        this.title = title;
        this.questions = questions;
    }

    public SurveyId getId() {
        return id;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getTitle() {
        return title;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
