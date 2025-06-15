package com.readingshare.survey.domain.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.common.exception.DifferentQuestionnaireComponentException; // 仕様書より
import com.readingshare.common.exception.DomainException;
import com.readingshare.survey.domain.model.Question;
import com.readingshare.survey.domain.model.Survey;
import com.readingshare.survey.domain.model.SurveyAnswer;
import com.readingshare.survey.domain.model.SurveyId;
import com.readingshare.survey.domain.repository.ISurveyAnswerRepository; // 必要に応じて追加
import com.readingshare.survey.domain.repository.ISurveyRepository;

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
     *
     * @param survey 保存するアンケートエンティティ
     * @return 保存されたアンケートエンティティ
     * @throws DomainException データベース登録失敗時
     */
    @Transactional
    public Survey saveSurvey(Survey survey) {
        // 例: 同じ部屋に同じタイトルのアンケートが存在しないかなどのチェック
        // if (surveyRepository.findByRoomIdAndTitle(survey.getRoomId(),
        // survey.getTitle()).isPresent()) {
        // throw new DomainException("A survey with the same title already exists in
        // this room.");
        // }
        try {
            return surveyRepository.save(survey);
        } catch (Exception e) {
            throw new DomainException("Failed to save survey due to a database error.", e);
        }
    }

    /**
     * アンケートのフォーマットを取得する。
     *
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
     *
     * @param surveyId 回答するアンケートID
     * @param userId   回答するユーザーID
     * @param answers  回答内容（質問IDと回答テキストのマップ）
     * @throws DomainException                          アンケートが見つからない、回答形式が不正、既に回答済みの場合など
     * @throws DifferentQuestionnaireComponentException いずれかの変数の欠損又は誤りのとき
     */
    @Transactional
    public void submitSurveyAnswer(Long surveyId, Long userId, Map<String, String> answers) {
        Optional<Survey> surveyOptional = surveyRepository.findById(new SurveyId(surveyId.toString()));
        if (surveyOptional.isEmpty()) {
            throw new DomainException("Survey not found with ID: " + surveyId);
        }
        Survey survey = surveyOptional.get();

        // 回答が既に存在するかチェック
        if (surveyAnswerRepository.findBySurveyIdAndResponderUserId(surveyId, userId).isPresent()) {
            throw new DomainException("User " + userId + " has already submitted an answer for survey " + surveyId);
        }

        // 回答内容のバリデーション
        if (answers == null || answers.isEmpty()) {
            throw new DifferentQuestionnaireComponentException("Answers cannot be null or empty.");
        }

        // 回答フォーマットの変換とバリデーション
        Map<Integer, Integer> convertedAnswers = new HashMap<>();
        List<Question> questions = survey.getQuestions();

        for (Map.Entry<String, String> entry : answers.entrySet()) {
            try {
                int questionIndex = Integer.parseInt(entry.getKey());
                int answerIndex = Integer.parseInt(entry.getValue());

                // 質問のインデックスが有効か確認
                if (questionIndex < 0 || questionIndex >= questions.size()) {
                    throw new DifferentQuestionnaireComponentException(
                            "Invalid question index: " + questionIndex);
                }

                Question question = questions.get(questionIndex);
                // 回答のインデックスが有効か確認（選択肢の範囲内か）
                if (answerIndex < 0 || answerIndex >= question.getOptions().size()) {
                    throw new DifferentQuestionnaireComponentException(
                            "Invalid answer index for question " + questionIndex + ": " + answerIndex);
                }

                convertedAnswers.put(questionIndex, answerIndex);
            } catch (NumberFormatException e) {
                throw new DifferentQuestionnaireComponentException(
                        "Invalid format for question or answer index");
            }
        }

        // すべての質問に回答があることを確認
        if (convertedAnswers.size() != questions.size()) {
            throw new DifferentQuestionnaireComponentException(
                    "Not all questions have been answered");
        }

        // 回答を保存
        SurveyAnswer surveyAnswer = new SurveyAnswer(
                new SurveyId(surveyId.toString()),
                userId.toString(),
                convertedAnswers);

        try {
            surveyAnswerRepository.save(surveyAnswer);
        } catch (Exception e) {
            throw new DomainException("Failed to save survey answer", e);
        }
    }
}
