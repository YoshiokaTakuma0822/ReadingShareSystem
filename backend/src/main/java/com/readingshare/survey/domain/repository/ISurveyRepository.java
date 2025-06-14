package com.readingshare.survey.domain.repository;

import com.readingshare.survey.domain.model.Survey;
import com.readingshare.survey.domain.model.SurveyAnswer;
import com.readingshare.survey.domain.model.SurveyId;

import java.util.List;
import java.util.Optional;

/**
 * アンケートリポジトリのインターフェース。
 * 内部設計書 C8 アンケート情報管理部 に対応。
 */
public interface ISurveyRepository {
    /**
     * アンケートを保存する。
     * @param survey 保存するアンケート
     */
    void save(Survey survey);

    /**
     * アンケート回答を保存する。
     * @param surveyAnswer 保存する回答
     */
    void saveAnswer(SurveyAnswer surveyAnswer);

    /**
     * IDでアンケートを検索する。
     * @param id 検索するアンケートのID
     * @return 見つかったアンケート(Optional)
     */
    Optional<Survey> findById(SurveyId id);

    /**
     * アンケートIDに紐づく全ての回答を取得する。
     * @param surveyId 検索するアンケートのID
     * @return 回答のリスト
     */
    List<SurveyAnswer> findAnswersBySurveyId(SurveyId surveyId);
}
