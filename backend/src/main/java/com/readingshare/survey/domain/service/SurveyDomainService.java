package com.readingshare.survey.domain.service;

import com.readingshare.survey.domain.model.Survey;
import com.readingshare.survey.domain.model.SurveyAnswer;
import com.readingshare.survey.domain.model.SurveyId;
import com.readingshare.survey.domain.repository.ISurveyRepository;
import com.readingshare.survey.domain.repository.ISurveyAnswerRepository; // 必要に応じて追加
import com.readingshare.common.exception.DomainException;
import com.readingshare.common.exception.DifferentQuestionnaireComponentException; // 仕様書より
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

/**
 * アンケートに関するドメインロジックを扱うサービス。
 * 担当: 成田
 */
@Service
public class SurveyDomainService {

    private final ISurveyRepository surveyRepository;
    private final ISurveyAnswerRepository surveyAnswerRepository;

    public SurveyDomainService(ISurveyRepository surveyRepository, ISurveyAnswerRepository surveyAnswerRepository) {
        this.surveyRepository = surveyRepository;
        this.surveyAnswerRepository = surveyAnswerRepository;
    }

    /**
     * 新しいアンケートをデータベースに保存する。
     * @param survey 保存するアンケートエンティティ
     * @return 保存されたアンケートエンティティ
     * @throws DomainException データベース登録失敗時
     */
    @Transactional
    public Survey saveSurvey(Survey survey) {
        // 例: 同じ部屋に同じタイトルのアンケートが存在しないかなどのチェック
        // if (surveyRepository.findByRoomIdAndTitle(survey.getRoomId(), survey.getTitle()).isPresent()) {
        //     throw new DomainException("A survey with the same title already exists in this room.");
        // }
        try {
            return surveyRepository.save(survey);
        } catch (Exception e) {
            throw new DomainException("Failed to save survey due to a database error.", e);
        }
    }

    /**
     * アンケートのフォーマットを取得する。
     * @param surveyId アンケートID
     * @return アンケートが見つかった場合はOptionalにSurvey、見つからない場合はOptional.empty()
     * @throws DomainException データベースアクセスエラー時
     */
    @Transactional(readOnly = true)
    public Optional<Survey> getSurveyFormat(SurveyId surveyId) {
        try {
            return surveyRepository.findById(surveyId);
        } catch (Exception e) {
            throw new DomainException("Failed to retrieve survey format from database.", e);
        }
    }


    /**
     * ユーザーが特定のアンケートに回答を提出する。
     * @param surveyId 回答するアンケートID
     * @param userId 回答するユーザーID
     * @param answers 回答内容（質問IDと回答テキストのマップ）
     * @throws DomainException アンケートが見つからない、回答形式が不正、既に回答済みの場合など
     * @throws DifferentQuestionnaireComponentException いずれかの変数の欠損又は誤りのとき
     */
    @Transactional
    public void submitSurveyAnswer(Long surveyId, Long userId, Map<String, String> answers) {
        Optional<Survey> surveyOptional = surveyRepository.findById(new SurveyId(surveyId));
        if (surveyOptional.isEmpty()) {
            throw new DomainException("Survey not found with ID: " + surveyId);
        }
        // Survey survey = surveyOptional.get(); // 使われていないためコメントアウト

        // 回答が既に存在するかチェック
        if (surveyAnswerRepository.findBySurveyIdAndResponderUserId(surveyId, userId).isPresent()) {
            throw new DomainException("User " + userId + " has already submitted an answer for survey " + surveyId);
        }

        // 回答内容のバリデーション（例: 必須質問の有無、選択肢の有効性など）
        // 内部設計書の「いずれかの変数の欠損又は誤りのときDifferntQuestionnaireComponentException」に対応
        if (answers == null || answers.isEmpty()) {
            throw new DifferentQuestionnaireComponentException("Answers cannot be null or empty.");
        }
        
        // 実際には、Surveyのquestionsとanswersのキーを照合し、整合性を確認するロジックが必要
        // 例: for (Question q : survey.getQuestions()) { if (!answers.containsKey(q.getId().toString())) throw new DifferentQuestionnaireComponentException(...) }


        SurveyAnswer newAnswer = new SurveyAnswer(null, surveyId, userId, answers, Instant.now());
        surveyAnswerRepository.save(newAnswer);
    }
}