package com.readingshare.survey.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.readingshare.survey.domain.model.Survey;
import com.readingshare.survey.domain.model.SurveyAnswer;
import com.readingshare.survey.domain.repository.ISurveyRepository;

@Repository
public class SurveyRepositoryImpl implements ISurveyRepository {
    @Autowired
    @Lazy
    private SurveyRepository surveyRepository;
    @Autowired
    @Lazy
    private SurveyAnswerRepository surveyAnswerRepository;

    @Override
    public Survey save(Survey survey) {
        return surveyRepository.save(survey);
    }

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
}
