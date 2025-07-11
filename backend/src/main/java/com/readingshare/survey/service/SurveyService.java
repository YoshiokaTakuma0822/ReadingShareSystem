package com.readingshare.survey.service;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
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
import com.readingshare.survey.dto.CreateSurveyRequest.QuestionDto;
import com.readingshare.survey.dto.SubmitSurveyAnswerRequest;
import com.readingshare.survey.dto.SurveyResultResponse;
import com.readingshare.survey.exception.SurveyErrorCode;

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
            var roomOpt = roomRepository.findById(request.roomId());
            if (roomOpt.isEmpty()) {
                throw new ResourceNotFoundException("Room not found with id: " + request.roomId());
            }
            var room = roomOpt.get();

            // 部屋の時刻制約をチェック
            if (!room.isActiveTime()) {
                throw new ApplicationException(
                        SurveyErrorCode.ROOM_INACTIVE.name(),
                        "Surveys cannot be created outside the room's active time period");
            }

            // 選択肢のバリデーション（重複と最小数）
            for (QuestionDto q : request.questions()) {
                if (q.options().size() < 2) {
                    throw new ApplicationException(
                            SurveyErrorCode.TOO_FEW_OPTIONS.name(),
                            "Question '" + q.questionText() + "' must have at least two options.");
                }
                if (new HashSet<>(q.options()).size() != q.options().size()) {
                    throw new ApplicationException(
                            SurveyErrorCode.DUPLICATE_OPTIONS.name(),
                            "Question '" + q.questionText() + "' contains duplicate options.");
                }
            }

            List<Question> questions = request.questions().stream()
                    .map(q -> new Question(q.questionText(), q.options(), q.questionType(),
                            q.allowAnonymous(), q.allowAddOptions()))
                    .collect(Collectors.toList());
            Survey survey = new Survey(request.roomId(), request.title(), questions, request.endTime().toInstant());
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
        // Load survey to get roomId and validate existence
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found with id: " + surveyId));

        // アンケートの終了時刻をチェック
        if (survey.isExpired()) {
            throw new ApplicationException(
                    SurveyErrorCode.SURVEY_EXPIRED.name(),
                    "Survey has expired and no longer accepts responses");
        }

        SurveyAnswer answer = new SurveyAnswer(surveyId, request.userId(), request.answers());
        surveyRepository.saveAnswer(answer);
        // Notify updated survey results via WebSocket
        SurveyResultResponse result = getSurveyResult(surveyId);
        surveyNotificationService.sendSurveyResultNotification(survey.getRoomId().toString(), result);
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
        // Load survey and validate existence
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found with id: " + surveyId));

        // アンケートの終了時刻をチェック
        if (survey.isExpired()) {
            throw new ApplicationException(
                    SurveyErrorCode.SURVEY_EXPIRED.name(),
                    "Survey has expired and no longer accepts new options");
        }

        Question targetQuestion = survey.getQuestions().stream()
                .filter(q -> q.getQuestionText().equals(questionText))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + questionText));

        try {
            targetQuestion.addOption(newOption);
            surveyRepository.save(survey);
            // Notify updated survey results due to new option
            SurveyResultResponse result = getSurveyResult(surveyId);
            surveyNotificationService.sendSurveyResultNotification(survey.getRoomId().toString(), result);
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
        return new SurveyResultResponse(survey.getId(), survey.getTitle(), totalRespondents, questionResults,
                survey.getEndTime().atOffset(ZoneOffset.UTC), survey.isExpired());
    }
}
