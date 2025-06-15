package com.readingshare.survey.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.JoinColumn;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

/**
 * アンケート集約のルートエンティティ。
 * 要求仕様書「(3)アンケートを作成する」に対応。
 */
@Entity
@Table(name = "surveys")
public class Survey {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "room_id")
    private String roomId; // アンケートが実施される部屋のID

    @Column(name = "title")
    private String title; // アンケートのタイトル

    @ElementCollection
    @CollectionTable(name = "survey_questions", joinColumns = @JoinColumn(name = "survey_id"))
    private List<Question> questions; // 質問のリスト

    @Column(name = "created_at")
    private LocalDateTime createdAt; // アンケート作成日時

    public Survey() {}

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
        this.id = UUID.randomUUID().toString();
        this.roomId = roomId;
        this.title = title;
        this.questions = questions;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
