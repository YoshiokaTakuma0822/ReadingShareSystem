package com.readingshare.survey.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readingshare.common.exception.ApplicationException;
import com.readingshare.survey.domain.model.Survey;
import com.readingshare.survey.dto.CreateSurveyRequest;
import com.readingshare.survey.dto.SubmitSurveyAnswerRequest;
import com.readingshare.survey.dto.SurveyResultResponse;
import com.readingshare.survey.service.SurveyService;

/**
 * アンケート機能のRESTコントローラー
 * 担当: 成田
 */
@RestController
@RequestMapping("/api/surveys")
public class SurveyController {

    private final SurveyService surveyService;

    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    /**
     * W7 アンケート作成画面からのリクエストを処理
     * 新しいアンケートを作成する。
     *
     * @param request アンケート作成リクエスト
     * @return 作成されたアンケートのID
     */
    @PostMapping
    public ResponseEntity<UUID> createSurvey(@RequestBody CreateSurveyRequest request) {
        try {
            UUID surveyId = surveyService.createSurvey(request);
            return ResponseEntity.ok(surveyId);
        } catch (ApplicationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * W8 アンケート回答画面からのリクエストを処理
     * アンケートの回答を提出する。
     *
     * @param surveyId アンケートのID
     * @param request  アンケート回答リクエスト
     * @return 提出成功時はHTTP 200 OK
     */
    @PostMapping("/{surveyId}/answers")
    public ResponseEntity<Void> submitAnswer(
            @PathVariable UUID surveyId,
            @RequestBody SubmitSurveyAnswerRequest request) {
        try {
            surveyService.submitAnswer(surveyId, request);
            return ResponseEntity.ok().build();
        } catch (ApplicationException e) {
            return ResponseEntity.badRequest().build();
        }
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
        try {
            SurveyResultResponse result = surveyService.getSurveyResult(surveyId);
            return ResponseEntity.ok(result);
        } catch (ApplicationException e) {
            return ResponseEntity.notFound().build();
        }
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
        try {
            surveyService.addOption(surveyId, request.questionText(), request.newOption());
            return ResponseEntity.ok().build();
        } catch (ApplicationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 選択肢追加リクエストDTO
     */
    public record AddOptionRequest(String questionText, String newOption) {
    }
}
