package com.readingshare.survey.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.readingshare.survey.domain.model.Survey;
import com.readingshare.survey.domain.model.SurveyAnswer;

/**
 * アンケート情報の永続化を担当するリポジトリインターフェース。
 * 担当: 成田
 */
public interface ISurveyRepository {
    /**
     * アンケートを保存する
     */
    Survey save(Survey survey);

    /**
     * アンケート回答を保存する
     */
    SurveyAnswer saveAnswer(SurveyAnswer answer);

    /**
     * アンケートを検索する
     */
    Optional<Survey> findById(UUID id);

    /**
     * 特定の部屋のアンケート一覧を取得する
     */
    List<Survey> findByRoomId(UUID roomId);

    /**
     * アンケートの回答一覧を取得する
     */
    List<SurveyAnswer> findAnswersBySurveyId(UUID surveyId);
}
