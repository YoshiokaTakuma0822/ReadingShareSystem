package com.readingshare.survey.service;

import com.readingshare.survey.domain.model.Question;
import com.readingshare.survey.domain.model.Survey;
import com.readingshare.survey.domain.service.SurveyDomainService;
import com.readingshare.common.exception.ApplicationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * アンケート作成のアプリケーションサービス。
 * 担当: 成田
 */
@Service
public class CreateSurveyService {

    private final SurveyDomainService surveyDomainService;

    public CreateSurveyService(SurveyDomainService surveyDomainService) {
        this.surveyDomainService = surveyDomainService;
    }

    /**
     * 新しいアンケートを作成し、データベースに保存する。
     * @param roomId アンケートが関連付けられる部屋のID
     * @param creatorUserId アンケート作成者のユーザーID
     * @param title アンケートのタイトル
     * @param description アンケートの説明
     * @param questions アンケートの質問リスト
     * @return 保存されたアンケートエンティティ
     * @throws ApplicationException アンケート作成に失敗した場合
     */
    public Survey createSurvey(Long roomId, Long creatorUserId, String title, String description, List<Question> questions) {
        Survey newSurvey = new Survey(null, roomId, creatorUserId, title, description, Instant.now());
        newSurvey.setQuestions(questions); // 質問を設定
        return surveyDomainService.saveSurvey(newSurvey);
    }
}