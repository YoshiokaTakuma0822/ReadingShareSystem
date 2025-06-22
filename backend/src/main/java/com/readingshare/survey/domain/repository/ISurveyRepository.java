package com.readingshare.survey.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.readingshare.survey.domain.model.Survey;
import com.readingshare.survey.domain.model.SurveyAnswer;

/**
 * ISurveyRepository は、アンケート情報の永続化を担当するリポジトリインターフェースです。
 * アンケートの保存、検索、回答の保存などの操作を提供します。
 *
 * @author 02002
 * @componentId C4
 * @moduleName アンケートリポジトリ
 * @see Survey
 */
public interface ISurveyRepository {
    /**
     * アンケートを保存します。
     *
     * @param survey 保存するアンケート
     * @return 保存されたアンケート
     */
    Survey save(Survey survey);

    /**
     * アンケートの回答を保存します。
     *
     * @param answer 保存するアンケート回答
     * @return 保存されたアンケート回答
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

    /**
     * 特定のアンケートとユーザーの組み合わせで回答を検索します。
     *
     * @param surveyId アンケートID
     * @param userId   ユーザーID
     * @return 回答が見つかった場合はOptionalにSurveyAnswer、見つからない場合はOptional.empty()
     */
    Optional<SurveyAnswer> findBySurveyIdAndUserId(UUID surveyId, UUID userId);
}
