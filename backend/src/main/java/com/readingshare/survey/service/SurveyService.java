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

import com.readingshare.common.exception.ApplicationException;
import com.readingshare.common.exception.ResourceNotFoundException;
import com.readingshare.room.domain.repository.IRoomRepository;
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
    private final IRoomRepository roomRepository;

    public SurveyService(ISurveyRepository surveyRepository, IRoomRepository roomRepository) {
        this.surveyRepository = surveyRepository;
        this.roomRepository = roomRepository;
    }

    // --- アンケート作成 ---
    @Transactional
    public UUID createSurvey(CreateSurveyRequest request) {
        try {
            // roomIdが存在するかチェック
            if (!roomRepository.findById(request.roomId()).isPresent()) {
                throw new ResourceNotFoundException("Room not found with id: " + request.roomId());
            }

            List<Question> questions = request.questions().stream()
                    .map(q -> new Question(q.questionText(), q.options(), q.questionType(),
                            q.allowAnonymous(), q.allowAddOptions()))
                    .collect(Collectors.toList());
            Survey survey = new Survey(request.roomId(), request.title(), questions);
            Survey savedSurvey = surveyRepository.save(survey);
            return savedSurvey.getId();
        } catch (IllegalArgumentException e) {
            throw new ApplicationException(e.getMessage(), e);
        }
    }

    // Survey取得用メソッドを追加
    @Transactional(readOnly = true)
    public Survey getSurvey(UUID surveyId) {
        return surveyRepository.findById(surveyId)
            .orElseThrow(() -> new ResourceNotFoundException("Survey not found: " + surveyId));
    }

    // --- アンケート回答 ---
    @Transactional
    public void submitAnswer(UUID surveyId, SubmitSurveyAnswerRequest request) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found with id: " + surveyId));
        // 追加選択肢があればアンケート本体に反映
        if (request.addedOptions() != null) {
            for (var entry : request.addedOptions().entrySet()) {
                String questionText = entry.getKey();
                List<String> newOptions = entry.getValue();
                survey.getQuestions().stream()
                        .filter(q -> q.getQuestionText().equals(questionText) && q.isAllowAddOptions())
                        .findFirst()
                        .ifPresent(q -> {
                            for (String opt : newOptions) {
                                try {
                                    q.addOption(opt);
                                } catch (Exception ignored) {}
                            }
                        });
            }
            surveyRepository.save(survey); // 選択肢をDBに反映
        }
        SurveyAnswer answer = new SurveyAnswer(surveyId, request.userId(), request.answers(), request.isAnonymous());
        surveyRepository.saveAnswer(answer);
    }

    // --- アンケート結果取得 ---
    @Transactional(readOnly = true)
    public SurveyResultResponse getSurveyResult(UUID surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found with id: " + surveyId));
        List<SurveyAnswer> answers = surveyRepository.findAnswersBySurveyId(surveyId);
        return buildResultDto(survey, answers);
    }

    public Optional<Survey> getSurveyFormat(UUID surveyId) {
        return surveyRepository.findById(surveyId);
    }

    /**
     * アンケートの質問に新しい選択肢を追加する
     */
    @Transactional
    public void addOption(UUID surveyId, String questionText, String newOption) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found with id: " + surveyId));

        Question targetQuestion = survey.getQuestions().stream()
                .filter(q -> q.getQuestionText().equals(questionText))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + questionText));

        try {
            targetQuestion.addOption(newOption);
            surveyRepository.save(survey);
        } catch (IllegalStateException | IllegalArgumentException e) {
            throw new ApplicationException(e.getMessage(), e);
        }
    }

    public boolean hasUserAnswered(UUID surveyId, UUID userId) {
        if (surveyRepository instanceof com.readingshare.survey.infrastructure.persistence.SurveyRepositoryImpl impl) {
            return impl.getSurveyAnswerRepository().findBySurveyIdAndUserId(surveyId, userId).isPresent();
        }
        return false;
    }

    private SurveyResultResponse buildResultDto(Survey survey, List<SurveyAnswer> answers) {
        List<SurveyResultResponse.QuestionResultResponse> questionResults = new ArrayList<>();
        for (Question question : survey.getQuestions()) {
            Map<String, Long> votes = question.getOptions().stream()
                    .collect(Collectors.toMap(Function.identity(), option -> 0L));
            for (SurveyAnswer answer : answers) {
                List<String> selectedOptions = answer.getAnswers().get(question.getQuestionText());
                if (selectedOptions != null) {
                    for (String selectedOption : selectedOptions) {
                        if (selectedOption != null && question.getOptions().contains(selectedOption)) {
                            votes.computeIfPresent(selectedOption, (key, value) -> value + 1);
                        }
                    }
                }
            }
            questionResults.add(new SurveyResultResponse.QuestionResultResponse(question.getQuestionText(), votes));
        }
        int totalRespondents = (int) answers.stream().map(SurveyAnswer::getUserId).distinct().count();
        return new SurveyResultResponse(survey.getId(), survey.getTitle(), totalRespondents, questionResults);
    }
}
