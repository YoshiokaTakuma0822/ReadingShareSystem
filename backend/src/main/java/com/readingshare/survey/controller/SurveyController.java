package com.readingshare.survey.controller;

import com.readingshare.survey.service.CreateSurveyService;
import com.readingshare.survey.service.GetSurveyResultService;
import com.readingshare.survey.service.SubmitSurveyAnswerService;
import com.readingshare.survey.service.dto.CreateSurveyRequest;
import com.readingshare.survey.service.dto.SubmitSurveyAnswerRequest;
import com.readingshare.survey.service.dto.SurveyResultDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/surveys")
public class SurveyController {

    private final CreateSurveyService createSurveyService;
    private final SubmitSurveyAnswerService submitSurveyAnswerService;
    private final GetSurveyResultService getSurveyResultService;

    public SurveyController(CreateSurveyService createSurveyService,
                            SubmitSurveyAnswerService submitSurveyAnswerService,
                            GetSurveyResultService getSurveyResultService) {
        this.createSurveyService = createSurveyService;
        this.submitSurveyAnswerService = submitSurveyAnswerService;
        this.getSurveyResultService = getSurveyResultService;
    }

    /**
     * W7 アンケート作成画面からのリクエストを処理
     */
    @PostMapping
    public ResponseEntity<Void> createSurvey(@RequestBody CreateSurveyRequest request) {
        createSurveyService.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * W8 アンケート回答画面からのリクエストを処理
     */
    @PostMapping("/{surveyId}/answers")
    public ResponseEntity<Void> submitAnswer(
            @PathVariable String surveyId,
            @RequestBody SubmitSurveyAnswerRequest request) {
        submitSurveyAnswerService.execute(surveyId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * W9 アンケート結果画面のためのデータ取得
     */
    @GetMapping("/{surveyId}/results")
    public ResponseEntity<SurveyResultDto> getSurveyResult(@PathVariable String surveyId) {
        SurveyResultDto result = getSurveyResultService.execute(surveyId);
        return ResponseEntity.ok(result);
    }
}
