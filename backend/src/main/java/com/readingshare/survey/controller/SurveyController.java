package com.readingshare.survey.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readingshare.survey.application.service.SurveyService;
import com.readingshare.survey.domain.model.Survey;
import com.readingshare.survey.dto.CreateSurveyRequest;
import com.readingshare.survey.dto.SubmitSurveyAnswerRequest;
import com.readingshare.survey.dto.SurveyResultDto;

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
     * @return 作成されたアンケート情報
     */
    @PostMapping
    public ResponseEntity<Void> createSurvey(@RequestBody CreateSurveyRequest request) {
        surveyService.createSurvey(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
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
            @PathVariable String surveyId,
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
    public ResponseEntity<SurveyResultDto> getSurveyResult(@PathVariable String surveyId) {
        SurveyResultDto result = surveyService.getSurveyResult(surveyId);
        return ResponseEntity.ok(result);
    }

    /**
     * アンケートのフォーマット（質問内容）を取得する。
     *
     * @param surveyId アンケートのID
     * @return アンケートフォーマット
     */
    @GetMapping("/{surveyId}/format")
    public ResponseEntity<Survey> getSurveyFormat(@PathVariable String surveyId) {
        return surveyService.getSurveyFormat(surveyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
