package com.readingshare.survey.service;

import com.readingshare.survey.domain.model.Survey;
import com.readingshare.survey.domain.model.SurveyId;
import com.readingshare.survey.domain.repository.ISurveyRepository;
import com.readingshare.common.exception.DatabaseAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * アンケートフォーマット取得のアプリケーションサービス。
 * 担当: 成田
 */
@Service
public class GetSurveyFormatService {

    private final ISurveyRepository surveyRepository;

    public GetSurveyFormatService(ISurveyRepository surveyRepository) {
        this.surveyRepository = surveyRepository;
    }

    /**
     * 特定のアンケートのフォーマット（質問内容）を取得する。
     * @param surveyId 取得するアンケートID
     * @return アンケートが見つかった場合はOptionalにSurvey、見つからない場合はOptional.empty()
     * @throws DatabaseAccessException データベースアクセスエラー時
     */
    @Transactional(readOnly = true)
    public Optional<Survey> getSurveyFormat(Long surveyId) {
        return surveyRepository.findById(new SurveyId(surveyId));
    }
}