package com.readingshare.survey.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.readingshare.survey.domain.model.Survey;
import com.readingshare.survey.domain.model.SurveyAnswer;
import com.readingshare.survey.domain.repository.ISurveyRepository;

/**
 * SurveyRepositoryImpl は、ISurveyRepository の実装クラスです。
 * データベース操作を通じてアンケート情報を管理します。
 */
@Repository
public class SurveyRepositoryImpl implements ISurveyRepository {
    @Autowired
    private SurveyJpaRepository surveyRepository;

    @Autowired
    private SurveyAnswerJpaRepository surveyAnswerRepository;

    /**
     * アンケートを保存します。
     *
     * @param survey 保存するアンケート
     * @return 保存されたアンケート
     */
    @Override
    public Survey save(Survey survey) {
        return surveyRepository.save(survey);
    }

    /**
     * アンケートの回答を保存します。
     *
     * @param answer 保存するアンケート回答
     * @return 保存されたアンケート回答
     */
    @Override
    public SurveyAnswer saveAnswer(SurveyAnswer answer) {
        return surveyAnswerRepository.save(answer);
    }

    @Override
    public Optional<Survey> findById(UUID id) {
        return surveyRepository.findById(id);
    }

    @Override
    public List<Survey> findByRoomId(UUID roomId) {
        return surveyRepository.findByRoomIdOrderByCreatedAtDesc(roomId);
    }

    @Override
    public List<SurveyAnswer> findAnswersBySurveyId(UUID surveyId) {
        return surveyAnswerRepository.findBySurveyId(surveyId);
    }

    @Override
    public Optional<SurveyAnswer> findBySurveyIdAndUserId(UUID surveyId, UUID userId) {
        return surveyAnswerRepository.findBySurveyIdAndUserId(surveyId, userId);
    }
}
