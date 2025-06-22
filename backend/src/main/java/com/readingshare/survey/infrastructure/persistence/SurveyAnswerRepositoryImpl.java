package com.readingshare.survey.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.readingshare.survey.domain.model.SurveyAnswer;
import com.readingshare.survey.domain.repository.ISurveyAnswerRepository;

/**
 * SurveyAnswerRepositoryImpl は、アンケート回答情報のリポジトリ実装クラスです。
 *
 * @author 02002
 * @componentId C8
 * @moduleName アンケート回答リポジトリ実装
 * @see SurveyAnswerJpaRepository
 */
@Repository
public class SurveyAnswerRepositoryImpl implements ISurveyAnswerRepository {
    @Autowired
    private SurveyAnswerJpaRepository surveyAnswerRepository;

    /**
     * アンケート回答を保存します。
     *
     * @param surveyAnswer 保存するアンケート回答
     * @return 保存されたアンケート回答
     */
    @Override
    public SurveyAnswer save(SurveyAnswer surveyAnswer) {
        return surveyAnswerRepository.save(surveyAnswer);
    }

    /**
     * 指定されたアンケート ID に関連付けられたすべてのアンケート回答を取得します。
     *
     * @param surveyId アンケートの一意の識別子
     * @return アンケート ID に関連付けられたアンケート回答のリスト
     */
    @Override
    public List<SurveyAnswer> findBySurveyId(UUID surveyId) {
        return surveyAnswerRepository.findBySurveyId(surveyId);
    }

    /**
     * 指定されたアンケート ID と回答者ユーザー ID に関連付けられたアンケート回答を取得します。
     *
     * @param surveyId        アンケートの一意の識別子
     * @param responderUserId 回答者ユーザーの一意の識別子
     * @return アンケート ID と回答者ユーザー ID に関連付けられたアンケート回答
     */
    @Override
    public Optional<SurveyAnswer> findBySurveyIdAndResponderUserId(UUID surveyId, UUID responderUserId) {
        // SurveyAnswerRepositoryのfindBySurveyIdAndUserIdを利用
        return surveyAnswerRepository.findBySurveyIdAndUserId(surveyId, responderUserId);
    }
}
