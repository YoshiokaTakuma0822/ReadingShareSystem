package com.readingshare.survey.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readingshare.survey.domain.model.SurveyAnswer;

/**
 * アンケート回答情報のJPAリポジトリインターフェース。
 * 担当: 成田
 */
@Repository
public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, UUID> {

    /**
     * 特定のアンケートの全回答を取得する。
     *
     * @param surveyId アンケートID
     * @return 取得された回答リスト
     */
    List<SurveyAnswer> findBySurveyId(UUID surveyId);

    /**
     * 特定のアンケートとユーザーの組み合わせで回答を検索する。
     *
     * @param surveyId アンケートID
     * @param userId   ユーザーID
     * @return 回答が見つかった場合はOptionalにSurveyAnswer、見つからない場合はOptional.empty()
     */
    Optional<SurveyAnswer> findBySurveyIdAndUserId(UUID surveyId, UUID userId);
}
