package com.readingshare.survey.infrastructure.persistence;

import com.readingshare.survey.domain.model.Survey;
import com.readingshare.survey.domain.model.SurveyAnswer;
import com.readingshare.survey.domain.model.SurveyId;
import com.readingshare.survey.domain.repository.ISurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    public Optional<Survey> findById(SurveyId id) {
        return surveyRepository.findById(id);
    }

    @Override
    public List<Survey> findByRoomId(String roomId) {
        return surveyRepository.findByRoomIdOrderByCreatedAtDesc(Long.valueOf(roomId));
    }

    @Override
    public List<SurveyAnswer> findAnswersBySurveyId(SurveyId surveyId) {
        return surveyAnswerRepository.findBySurveyId(Long.valueOf(surveyId.getValue()));
    }
}
