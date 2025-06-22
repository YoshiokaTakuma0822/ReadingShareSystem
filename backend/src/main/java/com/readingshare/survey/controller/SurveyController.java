package com.readingshare.survey.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.readingshare.survey.domain.model.Survey;
import com.readingshare.survey.dto.CreateSurveyRequest;
import com.readingshare.survey.dto.SubmitSurveyAnswerRequest;
import com.readingshare.survey.dto.SurveyResultResponse;
import com.readingshare.survey.service.SurveyService;

/**
 * SurveyController は、アンケート機能のRESTコントローラーです。
 * アンケートの作成、回答、結果取得などの操作を提供します。
 *
 * @author 23002
 */
@RestController
@RequestMapping("/api/surveys")
public class SurveyController {

    private final SurveyService surveyService;

    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    /**
     * 新しいアンケートを作成します。
     *
     * @param request アンケート作成リクエスト
     * @return 作成されたアンケートのID
     */
    @PostMapping
    public ResponseEntity<UUID> createSurvey(@RequestBody CreateSurveyRequest request) {
        UUID surveyId = surveyService.createSurvey(request);
        return ResponseEntity.ok(surveyId);
    }

    /**
     * アンケートの回答を提出します。
     *
     * @param surveyId アンケートのID
     * @param request  アンケート回答リクエスト
     * @return 提出成功時はHTTP 200 OK
     */
    @PostMapping("/{surveyId}/answers")
    public ResponseEntity<Void> submitAnswer(
            @PathVariable UUID surveyId,
            @RequestBody SubmitSurveyAnswerRequest request) {
        surveyService.submitAnswer(surveyId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * W9 アンケート結果画面のためのデータ取得
     * アンケートの集計結果を取得する。
     *
     * @param surveyId アンケートのID
     * @return アンケート結果DTO
     */
    @GetMapping("/{surveyId}/results")
    public ResponseEntity<SurveyResultResponse> getSurveyResult(@PathVariable UUID surveyId) {
        SurveyResultResponse result = surveyService.getSurveyResult(surveyId);
        return ResponseEntity.ok(result);
    }

    /**
     * アンケートのフォーマット（質問内容）を取得する。
     *
     * @param surveyId アンケートのID
     * @return アンケートフォーマット
     */
    @GetMapping("/{surveyId}/format")
    public ResponseEntity<Survey> getSurveyFormat(@PathVariable UUID surveyId) {
        return surveyService.getSurveyFormat(surveyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * アンケートの質問に新しい選択肢を追加する。
     *
     * @param surveyId アンケートのID
     * @param request  選択肢追加リクエスト
     * @return 追加成功時はHTTP 200 OK
     */
    @PostMapping("/{surveyId}/options")
    public ResponseEntity<Void> addOption(@PathVariable UUID surveyId,
            @RequestBody AddOptionRequest request) {
        surveyService.addOption(surveyId, request.questionText(), request.newOption());
        return ResponseEntity.ok().build();
    }

    /**
     * ユーザーがアンケートに回答済みかどうかを判定する。
     *
     * @param surveyId アンケートのID
     * @param userId   ユーザーのID
     * @return 回答済みならばHTTP 200 OK と true、未回答ならば false
     */
    @GetMapping("/{surveyId}/answered")
    public ResponseEntity<Boolean> hasUserAnswered(
            @PathVariable UUID surveyId,
            @RequestParam UUID userId) {
        boolean answered = surveyService.hasUserAnswered(surveyId, userId);
        return ResponseEntity.ok(answered);
    }

    /**
     * 選択肢追加リクエストDTO
     */
    public record AddOptionRequest(String questionText, String newOption) {
    }
}
