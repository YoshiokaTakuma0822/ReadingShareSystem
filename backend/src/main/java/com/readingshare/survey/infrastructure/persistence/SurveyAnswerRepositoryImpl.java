package com.readingshare.survey.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.readingshare.survey.domain.model.SurveyAnswer;
import com.readingshare.survey.domain.repository.ISurveyAnswerRepository;

@Repository
public class SurveyAnswerRepositoryImpl implements ISurveyAnswerRepository {
    @Autowired
    @Lazy
    private SurveyAnswerJpaRepository surveyAnswerRepository;

    @Override
    public SurveyAnswer save(SurveyAnswer surveyAnswer) {
        return surveyAnswerRepository.save(surveyAnswer);
    }

    @Override
    public List<SurveyAnswer> findBySurveyId(UUID surveyId) {
        return surveyAnswerRepository.findBySurveyId(surveyId);
    }

    @Override
    public Optional<SurveyAnswer> findBySurveyIdAndResponderUserId(UUID surveyId, UUID responderUserId) {
        // SurveyAnswerRepositoryのfindBySurveyIdAndUserIdを利用
        return surveyAnswerRepository.findBySurveyIdAndUserId(surveyId, responderUserId);
    }
}
