package com.readingshare.survey.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.readingshare.survey.domain.model.SurveyAnswer;
import com.readingshare.survey.domain.repository.ISurveyAnswerRepository;

/**
 * アンケート回答情報のリポジトリ実装クラス。
 * ドメイン層のISurveyAnswerRepositoryを実装し、JPAリポジトリに委譲する。
 */
@Repository
public class SurveyAnswerRepositoryImpl implements ISurveyAnswerRepository {
    @Autowired
    @Lazy
    private SurveyAnswerJpaRepository surveyAnswerJpaRepository;

    /**
     * アンケート回答を保存する。
     */
    @Override
    public SurveyAnswer save(SurveyAnswer surveyAnswer) {
        return surveyAnswerJpaRepository.save(surveyAnswer);
    }

    /**
     * 特定のアンケートの全回答を取得する。
     */
    @Override
    public List<SurveyAnswer> findBySurveyId(UUID surveyId) {
        return surveyAnswerJpaRepository.findBySurveyId(surveyId);
    }

    /**
     * 特定のアンケートとユーザーの組み合わせで回答を検索する。
     */
    @Override
    public Optional<SurveyAnswer> findBySurveyIdAndResponderUserId(UUID surveyId, UUID responderUserId) {
        // SurveyAnswerJpaRepositoryのfindBySurveyIdAndUserIdを利用
        return surveyAnswerJpaRepository.findBySurveyIdAndUserId(surveyId, responderUserId);
    }
}
