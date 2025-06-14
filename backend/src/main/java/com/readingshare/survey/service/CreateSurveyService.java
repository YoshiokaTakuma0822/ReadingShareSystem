package com.readingshare.survey.service;

import com.readingshare.survey.domain.model.Question;
import com.readingshare.survey.domain.model.Survey;
import com.readingshare.survey.domain.repository.ISurveyRepository;
import com.readingshare.survey.service.dto.CreateSurveyRequest;
import com.readingshare.survey.service.exceptions.InvalidSurveyComponentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * ユースケース: アンケートを作成する
 * 内部設計書 M8 アンケート情報管理のsaveメソッドに相当。
 */
@Service
@RequiredArgsConstructor
public class CreateSurveyService {

    private final ISurveyRepository surveyRepository;

    @Transactional
    public void execute(CreateSurveyRequest request) {
        try {
            List<Question> questions = request.questions().stream()
                .map(q -> new Question(q.questionText(), q.options()))
                .collect(Collectors.toList());

            Survey survey = new Survey(request.roomId(), request.title(), questions);

            surveyRepository.save(survey);
        } catch (IllegalArgumentException e) {
            // 内部設計書のエラー処理 `DifferntQuestionnaireComponet` に対応
            throw new InvalidSurveyComponentException(e.getMessage(), e);
        }
    }
}
