package com.readingshare.survey.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.survey.domain.model.Question;
import com.readingshare.survey.domain.model.Survey;
import com.readingshare.survey.domain.model.SurveyAnswer;
import com.readingshare.survey.domain.repository.ISurveyRepository;
import com.readingshare.survey.dto.CreateSurveyRequest;
import com.readingshare.survey.dto.SubmitSurveyAnswerRequest;
import com.readingshare.survey.dto.SurveyResultResponse;

/**
 * アンケート関連サービスを1ファイルに統合
 */
@Service
public class SurveyService {
    private final ISurveyRepository surveyRepository;

    public SurveyService(ISurveyRepository surveyRepository) {
        this.surveyRepository = surveyRepository;
    }

    // --- アンケート作成 ---
    @Transactional
    public void createSurvey(CreateSurveyRequest request) {
        try {
            List<Question> questions = request.questions().stream()
                    .map(q -> new Question(q.questionText(), q.options()))
                    .collect(Collectors.toList());
            Survey survey = new Survey(request.roomId(), request.title(), questions);
            surveyRepository.save(survey);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    // --- アンケート回答 ---
    @Transactional
    public void submitAnswer(UUID surveyId, SubmitSurveyAnswerRequest request) {
        surveyRepository.findById(surveyId)
                .orElseThrow(() -> new RuntimeException("Survey not found with id: " + surveyId));
        SurveyAnswer answer = new SurveyAnswer(surveyId, request.userId(), request.answers());
        surveyRepository.saveAnswer(answer);
    }

    // --- アンケート結果取得 ---
    @Transactional(readOnly = true)
    public SurveyResultResponse getSurveyResult(UUID surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new RuntimeException("Survey not found with id: " + surveyId));
        List<SurveyAnswer> answers = surveyRepository.findAnswersBySurveyId(surveyId);
        return buildResultDto(survey, answers);
    }

    public Optional<Survey> getSurveyFormat(UUID surveyId) {
        return surveyRepository.findById(surveyId);
    }

    private SurveyResultResponse buildResultDto(Survey survey, List<SurveyAnswer> answers) {
        List<SurveyResultResponse.QuestionResultResponse> questionResults = new ArrayList<>();
        for (int i = 0; i < survey.getQuestions().size(); i++) {
            Question question = survey.getQuestions().get(i);
            final int questionIndex = i;
            Map<String, Long> votes = question.getOptions().stream()
                    .collect(Collectors.toMap(Function.identity(), option -> 0L));
            for (SurveyAnswer answer : answers) {
                Integer selectedOptionIndex = answer.getAnswers().get(questionIndex);
                if (selectedOptionIndex != null && selectedOptionIndex < question.getOptions().size()) {
                    String selectedOption = question.getOptions().get(selectedOptionIndex);
                    votes.computeIfPresent(selectedOption, (key, value) -> value + 1);
                }
            }
            questionResults.add(new SurveyResultResponse.QuestionResultResponse(question.getQuestionText(), votes));
        }
        int totalRespondents = (int) answers.stream().map(SurveyAnswer::getUserId).distinct().count();
        return new SurveyResultResponse(survey.getId(), survey.getTitle(), totalRespondents, questionResults);
    }
}
