package com.readingshare.survey.infrastructure.persistence;

import com.readingshare.survey.domain.model.SurveyAnswer;
import com.readingshare.survey.domain.repository.ISurveyAnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SurveyAnswerRepositoryImpl implements ISurveyAnswerRepository {
    @Autowired
    @Lazy
    private SurveyAnswerRepository surveyAnswerRepository;

    @Override
    public SurveyAnswer save(SurveyAnswer surveyAnswer) {
        return surveyAnswerRepository.save(surveyAnswer);
    }

    @Override
    public List<SurveyAnswer> findBySurveyId(Long surveyId) {
        return surveyAnswerRepository.findBySurveyId(surveyId);
    }

    @Override
    public Optional<SurveyAnswer> findBySurveyIdAndResponderUserId(Long surveyId, Long responderUserId) {
        // SurveyAnswerRepositoryのfindBySurveyIdAndUserIdを利用
        return surveyAnswerRepository.findBySurveyIdAndUserId(surveyId, responderUserId);
    }
}
