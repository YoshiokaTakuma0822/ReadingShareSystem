package com.readingshare.survey.controller;

import com.readingshare.survey.domain.model.Survey;
import com.readingshare.survey.domain.model.SurveyAnswer;
import com.readingshare.survey.service.CreateSurveyService;
import com.readingshare.survey.service.GetSurveyFormatService;
import com.readingshare.survey.service.GetSurveyResultService;
import com.readingshare.survey.service.SubmitSurveyAnswerService;
import com.readingshare.survey.service.dto.CreateSurveyRequest;
import com.readingshare.survey.service.dto.SubmitSurveyAnswerRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * アンケートに関するAPIを処理するコントローラー。
 * 担当: 成田
 */
@RestController
@RequestMapping("/api/surveys")
public class SurveyController {

    private final CreateSurveyService createSurveyService;
    private final GetSurveyFormatService getSurveyFormatService;
    private final SubmitSurveyAnswerService submitSurveyAnswerService;
    private final GetSurveyResultService getSurveyResultService;

    public SurveyController(CreateSurveyService createSurveyService,
                            GetSurveyFormatService getSurveyFormatService,
                            SubmitSurveyAnswerService submitSurveyAnswerService,
                            GetSurveyResultService getSurveyResultService) {
        this.createSurveyService = createSurveyService;
        this.getSurveyFormatService = getSurveyFormatService;
        this.submitSurveyAnswerService = submitSurveyAnswerService;
        this.getSurveyResultService = getSurveyResultService;
    }

    /**
     * 新しいアンケートを作成する。
     * @param request アンケート作成リクエスト
     * @return 作成されたアンケート情報
     */
    @PostMapping
    public ResponseEntity<Survey> createSurvey(@RequestBody CreateSurveyRequest request) {
        // TODO: userIdは認証情報から取得する
        Long currentUserId = 1L; // 仮のユーザーID
        Survey newSurvey = createSurveyService.createSurvey(
                request.getRoomId(),
                currentUserId,
                request.getTitle(),
                request.getDescription(),
                request.getQuestions()
        );
        return ResponseEntity.ok(newSurvey);
    }

    /**
     * 特定のアンケートのフォーマット（質問内容）を取得する。
     * @param surveyId アンケートID
     * @return アンケートフォーマット
     */
    @GetMapping("/{surveyId}/format")
    public ResponseEntity<Survey> getSurveyFormat(@PathVariable Long surveyId) {
        return getSurveyFormatService.getSurveyFormat(surveyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * アンケートの回答を提出する。
     * @param surveyId 回答するアンケートID
     * @param request アンケート回答リクエスト
     * @return 提出成功時はHTTP 200 OK
     */
    @PostMapping("/{surveyId}/answer")
    public ResponseEntity<String> submitSurveyAnswer(@PathVariable Long surveyId, @RequestBody SubmitSurveyAnswerRequest request) {
        // TODO: userIdは認証情報から取得する
        Long currentUserId = 2L; // 仮のユーザーID
        submitSurveyAnswerService.submitAnswer(surveyId, currentUserId, request.getAnswers());
        return ResponseEntity.ok("Survey answer submitted successfully.");
    }

    /**
     * 特定のアンケートの結果を取得する。
     * @param surveyId 結果を取得するアンケートID
     * @return アンケート結果（回答リスト）
     */
    @GetMapping("/{surveyId}/results")
    public ResponseEntity<List<SurveyAnswer>> getSurveyResults(@PathVariable Long surveyId) {
        List<SurveyAnswer> results = getSurveyResultService.getSurveyResults(surveyId);
        return ResponseEntity.ok(results);
    }
}