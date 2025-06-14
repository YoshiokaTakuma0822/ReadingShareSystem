package com.readingshare.survey.service;

import com.readingshare.survey.domain.model.Question;
import com.readingshare.survey.domain.model.Survey;
import com.readingshare.survey.domain.model.SurveyAnswer;
import com.readingshare.survey.domain.model.SurveyId;
import com.readingshare.survey.domain.repository.ISurveyRepository;
import com.readingshare.survey.service.dto.SurveyResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ユースケース: アンケートの結果を見る
 */
@Service
@RequiredArgsConstructor
public class GetSurveyResultService {

    private final ISurveyRepository surveyRepository;

    @Transactional(readOnly = true)
    public SurveyResultDto execute(String surveyIdValue) {
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

            // 各回答をループして票を集計
            for (SurveyAnswer answer : answers) {
                Integer selectedOptionIndex = answer.getAnswers().get(questionIndex);
                if (selectedOptionIndex != null && selectedOptionIndex < question.getOptions().size()) {
                    String selectedOption = question.getOptions().get(selectedOptionIndex);
                    votes.computeIfPresent(selectedOption, (key, value) -> value + 1);
                }
            }
            questionResults.add(new SurveyResultDto.QuestionResultDto(question.getQuestionText(), votes));
        }

        // 回答者数をユニークなユーザーIDでカウント
        int totalRespondents = (int) answers.stream().map(SurveyAnswer::getUserId).distinct().count();

        return new SurveyResultDto(survey.getId().getValue(), survey.getTitle(), totalRespondents, questionResults);
    }
}
