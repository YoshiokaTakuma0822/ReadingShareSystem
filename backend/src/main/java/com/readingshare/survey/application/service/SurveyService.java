package com.readingshare.survey.application.service;

import java.util.Optional;

import com.readingshare.survey.domain.model.Survey;
import com.readingshare.survey.dto.CreateSurveyRequest;
import com.readingshare.survey.dto.SubmitSurveyAnswerRequest;
import com.readingshare.survey.dto.SurveyResultDto;

/**
 * アンケート機能のアプリケーションサービスインターフェース
 * 担当: 成田
 */
public interface SurveyService {
    /**
     * アンケートを作成する
     */
    void createSurvey(CreateSurveyRequest request);

    /**
     * アンケートに回答を提出する
     */
    void submitAnswer(String surveyId, SubmitSurveyAnswerRequest request);

    /**
     * アンケート結果を取得する
     */
    SurveyResultDto getSurveyResult(String surveyId);

    /**
     * アンケートのフォーマットを取得する
     */
    Optional<Survey> getSurveyFormat(String surveyId);
}
