package com.readingshare.survey.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * アンケートエンティティ。
 */
@Entity
@Table(name = "surveys")
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // アンケートID (主キー)

    @NotNull
    @Column(name = "room_id", nullable = false)
    private Long roomId; // 関連する部屋ID

    @NotNull
    @Column(name = "creator_user_id", nullable = false)
    private Long creatorUserId; // アンケート作成者のユーザーID

    @NotNull
    @Column(name = "title", nullable = false)
    private String title; // アンケートタイトル

    @Column(name = "description")
    private String description; // アンケートの説明

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt; // 作成日時

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "survey_id") // SurveyのIDを外部キーとしてQuestionテーブルに保存
    private List<Question> questions = new ArrayList<>(); // 質問リスト

    // デフォルトコンストラクタ
    public Survey() {}

    public Survey(Long id, Long roomId, Long creatorUserId, String title, String description, Instant createdAt) {
        this.id = id;
        this.roomId = roomId;
        this.creatorUserId = creatorUserId;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Getters
    public Long getId() { return id; }
    public Long getRoomId() { return roomId; }
    public Long getCreatorUserId() { return creatorUserId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Instant getCreatedAt() { return createdAt; }
    public List<Question> getQuestions() { return questions; }

    // Setters (JPAのためにidは必要)
    public void setId(Long id) { this.id = id; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public void setCreatorUserId(Long creatorUserId) { this.creatorUserId = creatorUserId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setQuestions(List<Question> questions) {
        this.questions.clear();
        if (questions != null) {
            this.questions.addAll(questions);
        }
    }

    /**
     * 質問を追加するヘルパーメソッド。
     * @param question 追加する質問
     */
    public void addQuestion(Question question) {
        if (this.questions == null) {
            this.questions = new ArrayList<>();
        }
        this.questions.add(question);
    }
}