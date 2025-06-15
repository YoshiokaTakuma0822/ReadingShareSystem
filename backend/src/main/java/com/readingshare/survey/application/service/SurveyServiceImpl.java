package com.readingshare.survey.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.common.exception.ApplicationException;
import com.readingshare.survey.domain.model.Question;
import com.readingshare.survey.domain.model.Survey;
import com.readingshare.survey.domain.model.SurveyAnswer;
import com.readingshare.survey.domain.model.SurveyId;
import com.readingshare.survey.domain.repository.ISurveyRepository;
import com.readingshare.survey.dto.CreateSurveyRequest;
import com.readingshare.survey.dto.SubmitSurveyAnswerRequest;
import com.readingshare.survey.dto.SurveyResultDto;

/**
 * アンケート機能のアプリケーションサービス実装
 * 担当: 成田
 */
@Service
@Transactional
public class SurveyServiceImpl implements SurveyService {

    private final ISurveyRepository surveyRepository;

    public SurveyServiceImpl(ISurveyRepository surveyRepository) {
        this.surveyRepository = surveyRepository;
    }

    @Override
    public void createSurvey(CreateSurveyRequest request) {
        try {
            List<Question> questions = request.questions().stream()
                    .map(q -> new Question(q.questionText(), q.options()))
                    .collect(Collectors.toList());
            Survey survey = new Survey(request.roomId(), request.title(), questions);
            surveyRepository.save(survey);
        } catch (IllegalArgumentException e) {
            throw new ApplicationException("アンケートの作成に失敗しました: " + e.getMessage(), e);
        }
    }

    @Override
    public void submitAnswer(String surveyId, SubmitSurveyAnswerRequest request) {
        SurveyId id = new SurveyId(surveyId);
        surveyRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("指定されたアンケートが見つかりません: " + surveyId));
        SurveyAnswer answer = new SurveyAnswer(id, request.userId(), request.answers());
        surveyRepository.saveAnswer(answer);
    }

    @Override
    @Transactional(readOnly = true)
    public SurveyResultDto getSurveyResult(String surveyId) {
        SurveyId id = new SurveyId(surveyId);
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("指定されたアンケートが見つかりません: " + surveyId));
        List<SurveyAnswer> answers = surveyRepository.findAnswersBySurveyId(id);
        return buildResultDto(survey, answers);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Survey> getSurveyFormat(String surveyId) {
        return surveyRepository.findById(new SurveyId(surveyId));
    }

    private SurveyResultDto buildResultDto(Survey survey, List<SurveyAnswer> answers) {
        List<SurveyResultDto.QuestionResultDto> questionResults = new ArrayList<>();
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
            questionResults.add(new SurveyResultDto.QuestionResultDto(question.getQuestionText(), votes));
        }

        int totalRespondents = (int) answers.stream().map(SurveyAnswer::getUserId).distinct().count();
        return new SurveyResultDto(survey.getId().getValue(), survey.getTitle(), totalRespondents, questionResults);
    }
}
