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
    private final SurveyNotificationService surveyNotificationService;

    public SurveyService(ISurveyRepository surveyRepository, IRoomRepository roomRepository,
            SurveyNotificationService surveyNotificationService) {
        this.surveyRepository = surveyRepository;
        this.roomRepository = roomRepository;
        this.surveyNotificationService = surveyNotificationService;
    }

    // --- アンケート作成 ---
    @Transactional
    public UUID createSurvey(CreateSurveyRequest request, UUID creatorUserId) {
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

            // チャット通知を送信
            surveyNotificationService.sendSurveyCreatedNotification(savedSurvey, creatorUserId);

            return savedSurvey.getId();
        } catch (IllegalArgumentException e) {
            throw new ApplicationException(e.getMessage(), e);
        }
    }

    // --- アンケート回答 ---
    @Transactional
    public void submitAnswer(UUID surveyId, SubmitSurveyAnswerRequest request) {
        surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found with id: " + surveyId));
        SurveyAnswer answer = new SurveyAnswer(surveyId, request.userId(), request.answers());
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

    // --- 回答確認 ---
    public boolean hasAnswered(UUID surveyId, UUID userId) {
        return surveyRepository.findAnswerBySurveyIdAndUserId(surveyId, userId).isPresent();
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
