package com.readingshare.survey.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Surveyは、アンケートのエンティティクラスです。
 *
 * @author 02002
 * @componentId C4
 */
@Entity
@Table(name = "surveys")
public class Survey {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "room_id")
    private UUID roomId; // アンケートが実施される部屋のID

    @Column(name = "title")
    private String title; // アンケートのタイトル

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "questions", columnDefinition = "jsonb")
    private List<Question> questions; // 質問のリスト（JSONB形式）

    @Column(name = "created_at")
    private LocalDateTime createdAt; // アンケート作成日時

    public Survey() {
    }

    public Survey(UUID roomId, String title, List<Question> questions) {
        if (roomId == null) {
            throw new IllegalArgumentException("Room ID cannot be null.");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or empty.");
        }
        if (questions == null || questions.isEmpty()) {
            throw new IllegalArgumentException("Questions cannot be null or empty.");
        }
        this.id = UUID.randomUUID();
        this.roomId = roomId;
        this.title = title;
        this.questions = questions;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * アンケートの質問リストを取得します。
     *
     * @return 質問リスト
     */
    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public UUID getId() {
        return id;
    }

    public UUID getRoomId() {
        return roomId;
    }

    /**
     * アンケートのタイトルを取得します。
     *
     * @return アンケートのタイトル
     */
    public String getTitle() {
        return title;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
