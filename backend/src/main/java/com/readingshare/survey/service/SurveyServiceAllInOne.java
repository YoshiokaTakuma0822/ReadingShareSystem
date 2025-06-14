package main.java.com.readingshare.survey.service;

import com.readingshare.survey.domain.model.*;
import com.readingshare.survey.domain.repository.ISurveyRepository;
import com.readingshare.survey.service.dto.*;
import com.readingshare.survey.service.exceptions.InvalidSurveyComponentException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * アンケート関連サービスを1ファイルに統合
 */
@Service
public class SurveyServiceAllInOne {
    private final ISurveyRepository surveyRepository;

    public SurveyServiceAllInOne(ISurveyRepository surveyRepository) {
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
            throw new InvalidSurveyComponentException(e.getMessage(), e);
        }
    }

    // --- アンケート回答 ---
    @Transactional
    public void submitSurveyAnswer(String surveyIdValue, SubmitSurveyAnswerRequest request) {
        SurveyId surveyId = new SurveyId(surveyIdValue);
        surveyRepository.findById(surveyId)
            .orElseThrow(() -> new RuntimeException("Survey not found with id: " + surveyIdValue));
        SurveyAnswer answer = new SurveyAnswer(surveyId, request.userId(), request.answers());
        surveyRepository.saveAnswer(answer);
    }

    // --- アンケート結果取得 ---
    @Transactional(readOnly = true)
    public SurveyResultDto getSurveyResult(String surveyIdValue) {
        SurveyId surveyId = new SurveyId(surveyIdValue);
        Survey survey = surveyRepository.findById(surveyId)
            .orElseThrow(() -> new RuntimeException("Survey not found with id: " + surveyIdValue));
        List<SurveyAnswer> answers = surveyRepository.findAnswersBySurveyId(surveyId);
        return buildResultDto(survey, answers);
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
