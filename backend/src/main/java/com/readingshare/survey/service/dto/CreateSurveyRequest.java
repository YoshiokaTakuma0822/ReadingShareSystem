package com.readingshare.survey.service.dto;

import com.readingshare.survey.domain.model.Question;
import java.util.List;

/**
 * アンケート作成リクエストのDTO。
 */
public class CreateSurveyRequest {
    private Long roomId;
    private String title;
    private String description;
    private List<Question> questions; // 質問のリスト

    // Getters and Setters
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }
}