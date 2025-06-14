package com.readingshare.survey.controller;

import com.readingshare.survey.service.SurveyServiceAllInOne;
import com.readingshare.survey.dto.CreateSurveyRequest;
import com.readingshare.survey.dto.SubmitSurveyAnswerRequest;
import com.readingshare.survey.dto.SurveyResultDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/surveys")
public class SurveyController {

    private final SurveyServiceAllInOne surveyServiceAllInOne;

    public SurveyController(SurveyServiceAllInOne surveyServiceAllInOne) {
        this.surveyServiceAllInOne = surveyServiceAllInOne;
    }

    /**
     * W7 アンケート作成画面からのリクエストを処理
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
}
