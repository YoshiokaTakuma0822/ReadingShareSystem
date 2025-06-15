package com.readingshare.survey.domain.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.common.exception.DifferentQuestionnaireComponentException;
import com.readingshare.common.exception.DomainException;
import com.readingshare.survey.domain.model.Question;
import com.readingshare.survey.domain.model.Survey;
import com.readingshare.survey.domain.model.SurveyAnswer;
import com.readingshare.survey.domain.repository.ISurveyAnswerRepository;
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
        try {
            return surveyRepository.save(survey);
        } catch (Exception e) {
            throw new DomainException("Failed to save survey: " + e.getMessage(), e);
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
    public Optional<Survey> getSurveyFormat(UUID surveyId) {
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
    public void submitSurveyAnswer(UUID surveyId, UUID userId, Map<String, String> answers) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new DomainException("Survey not found"));

        // 既に回答済みかチェック
        if (surveyAnswerRepository.findBySurveyIdAndResponderUserId(surveyId, userId).isPresent()) {
            throw new DomainException("You have already answered this survey");
        }

        // 回答内容を検証
        Map<Integer, Integer> validatedAnswers = validateAnswers(survey.getQuestions(), answers);

        // 回答を保存
        SurveyAnswer surveyAnswer = new SurveyAnswer(surveyId, userId, validatedAnswers);
        surveyAnswerRepository.save(surveyAnswer);
    }

    /**
     * 回答内容を検証する。
     *
     * @param questions アンケートの質問リスト
     * @param answers   回答内容
     * @return 検証済みの回答内容
     * @throws DifferentQuestionnaireComponentException 回答内容が質問と一致しない場合
     */
    private Map<Integer, Integer> validateAnswers(List<Question> questions, Map<String, String> answers) {
        Map<Integer, Integer> validatedAnswers = new HashMap<>();

        for (Map.Entry<String, String> entry : answers.entrySet()) {
            int questionIndex = Integer.parseInt(entry.getKey());
            int selectedOptionIndex = Integer.parseInt(entry.getValue());

            // 質問のインデックスが正しいか確認
            if (questionIndex < 0 || questionIndex >= questions.size()) {
                throw new DifferentQuestionnaireComponentException("Invalid question index: " + questionIndex);
            }

            Question question = questions.get(questionIndex);
            // 選択肢のインデックスが正しいか確認
            if (selectedOptionIndex < 0 || selectedOptionIndex >= question.getOptions().size()) {
                throw new DifferentQuestionnaireComponentException("Invalid option index: " + selectedOptionIndex);
            }

            validatedAnswers.put(questionIndex, selectedOptionIndex);
        }

        return validatedAnswers;
    }
}
