package com.readingshare.survey.service;

import com.readingshare.survey.domain.model.SurveyAnswer;
import com.readingshare.survey.domain.model.SurveyId;
import com.readingshare.survey.domain.repository.ISurveyRepository;
import com.readingshare.survey.service.dto.SubmitSurveyAnswerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ユースケース: アンケートに回答する
 */
@Service
@RequiredArgsConstructor
public class SubmitSurveyAnswerService {

    private final ISurveyRepository surveyRepository;

    @Transactional
    public void execute(String surveyIdValue, SubmitSurveyAnswerRequest request) {
        SurveyId surveyId = new SurveyId(surveyIdValue);
        // アンケートが存在するかのチェック
        surveyRepository.findById(surveyId)
            .orElseThrow(() -> new RuntimeException("Survey not found with id: " + surveyIdValue));

        SurveyAnswer answer = new SurveyAnswer(surveyId, request.userId(), request.answers());
        surveyRepository.saveAnswer(answer);
    }
}
