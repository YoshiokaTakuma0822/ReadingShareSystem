package com.readingshare.survey.service;

import com.readingshare.survey.domain.service.SurveyDomainService;
import com.readingshare.common.exception.ApplicationException;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * アンケート回答提出のアプリケーションサービス。
 * 担当: 成田
 */
@Service
public class SubmitSurveyAnswerService {

    private final SurveyDomainService surveyDomainService;

    public SubmitSurveyAnswerService(SurveyDomainService surveyDomainService) {
        this.surveyDomainService = surveyDomainService;
    }

    /**
     * ユーザーが特定のアンケートに回答を提出する。
     * @param surveyId 回答するアンケートID
     * @param userId 回答するユーザーID
     * @param answers 回答内容（質問IDと回答テキストのマップ）
     * @throws ApplicationException アンケートが見つからない、回答形式が不正、既に回答済みの場合など
     */
    public void submitAnswer(Long surveyId, Long userId, Map<String, String> answers) {
        surveyDomainService.submitSurveyAnswer(surveyId, userId, answers);
    }
}