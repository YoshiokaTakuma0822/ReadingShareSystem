package com.readingshare.survey.service;

import com.readingshare.survey.domain.model.SurveyAnswer;
import com.readingshare.survey.domain.repository.ISurveyAnswerRepository; // 必要に応じて追加
import com.readingshare.common.exception.DatabaseAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * アンケート結果取得のアプリケーションサービス。
 * 担当: 成田
 */
@Service
public class GetSurveyResultService {

    private final ISurveyAnswerRepository surveyAnswerRepository;

    public GetSurveyResultService(ISurveyAnswerRepository surveyAnswerRepository) {
        this.surveyAnswerRepository = surveyAnswerRepository;
    }

    /**
     * 特定のアンケートの全回答結果を取得する。
     * @param surveyId 結果を取得するアンケートID
     * @return アンケート回答のリスト
     * @throws DatabaseAccessException データベースアクセスエラー時
     */
    @Transactional(readOnly = true)
    public List<SurveyAnswer> getSurveyResults(Long surveyId) {
        return surveyAnswerRepository.findBySurveyId(surveyId);
    }
}