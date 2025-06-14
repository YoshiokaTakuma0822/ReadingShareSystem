package com.readingshare.survey.domain.repository;

import com.readingshare.survey.domain.model.SurveyAnswer;

import java.util.List;
import java.util.Optional;

/**
 * アンケート回答情報の永続化を担当するリポジトリインターフェース。
 * 担当: 成田 (またはアンケート回答の機能に関連する担当者)
 */
public interface ISurveyAnswerRepository {

    /**
     * アンケート回答を保存する。
     * @param surveyAnswer 保存するアンケート回答エンティティ
     * @return 保存されたアンケート回答エンティティ
     */
    SurveyAnswer save(SurveyAnswer surveyAnswer);

    /**
     * 特定のアンケートの全回答を取得する。
     * @param surveyId アンケートID
     * @return アンケート回答のリスト
     */
    List<SurveyAnswer> findBySurveyId(Long surveyId);

    /**
     * 特定のアンケートと回答者ユーザーの組み合わせで回答を検索する。
     * @param surveyId アンケートID
     * @param responderUserId 回答者ユーザーID
     * @return 回答が見つかった場合はOptionalにSurveyAnswer、見つからない場合はOptional.empty()
     */
    Optional<SurveyAnswer> findBySurveyIdAndResponderUserId(Long surveyId, Long responderUserId);
}