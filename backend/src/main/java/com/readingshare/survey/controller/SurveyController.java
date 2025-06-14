package com.readingshare.survey.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readingshare.survey.domain.model.Survey;
import com.readingshare.survey.domain.model.SurveyAnswer;
import com.readingshare.survey.dto.CreateSurveyRequest;
import com.readingshare.survey.dto.SubmitSurveyAnswerRequest;
import com.readingshare.survey.dto.SurveyResultDto;
import com.readingshare.survey.service.SurveyServiceAllInOne;

@RestController
@RequestMapping("/api/surveys")
public class SurveyController {

    private final SurveyServiceAllInOne surveyServiceAllInOne;

    public SurveyController(SurveyServiceAllInOne surveyServiceAllInOne) {
        this.surveyServiceAllInOne = surveyServiceAllInOne;
    }

    /**
     * W7 アンケート作成画面からのリクエストを処理
     *
     * 新しいアンケートを作成する。
     *
     * @param request アンケート作成リクエスト
     * @return 作成されたアンケート情報
     */
    @PostMapping
    public ResponseEntity<Void> createSurvey(@RequestBody CreateSurveyRequest request) {
        surveyServiceAllInOne.createSurvey(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * W8 アンケート回答画面からのリクエストを処理
     */
    @PostMapping("/{surveyId}/answers")
    public ResponseEntity<Void> submitAnswer(
            @PathVariable String surveyId,
            @RequestBody SubmitSurveyAnswerRequest request) {
        surveyServiceAllInOne.submitSurveyAnswer(surveyId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * W9 アンケート結果画面のためのデータ取得
     */
    @GetMapping("/{surveyId}/results")
    public ResponseEntity<SurveyResultDto> getSurveyResult(@PathVariable String surveyId) {
        SurveyResultDto result = surveyServiceAllInOne.getSurveyResult(surveyId);
        return ResponseEntity.ok(result);
    }

    /**
     * 特定のアンケートのフォーマット（質問内容）を取得する。
     *
     * @param surveyId アンケートID
     * @return アンケートフォーマット
     */
    @GetMapping("/{surveyId}/format")
    public ResponseEntity<Survey> getSurveyFormat(@PathVariable Long surveyId) {
        return surveyServiceAllInOne.getSurveyFormat(surveyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * アンケートの回答を提出する。
     *
     * @param surveyId 回答するアンケートID
     * @param request  アンケート回答リクエスト
     * @return 提出成功時はHTTP 200 OK
     */
    @PostMapping("/{surveyId}/answer")
    public ResponseEntity<String> submitSurveyAnswer(@PathVariable Long surveyId,
            @RequestBody SubmitSurveyAnswerRequest request) {
        // TODO: userIdは認証情報から取得する
        Long currentUserId = 2L; // 仮のユーザーID
        surveyServiceAllInOne.submitAnswer(surveyId, currentUserId, request.getAnswers());
        return ResponseEntity.ok("Survey answer submitted successfully.");
    }

    /**
     * 特定のアンケートの結果を取得する。
     *
     * @param surveyId 結果を取得するアンケートID
     * @return アンケート結果（回答リスト）
     */
    @GetMapping("/{surveyId}/results")
    public ResponseEntity<List<SurveyAnswer>> getSurveyResults(@PathVariable Long surveyId) {
        List<SurveyAnswer> results = surveyServiceAllInOne.getSurveyResults(surveyId);
        return ResponseEntity.ok(results);
    }
}
