package com.readingshare.survey.infrastructure.persistance;

import com.readingshare.survey.domain.model.Survey;
import com.readingshare.survey.domain.model.SurveyAnswer;
import com.readingshare.survey.domain.model.SurveyId;
import com.readingshare.survey.domain.repository.ISurveyRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * アンケートリポジトリのインメモリ実装例。
 * 本番環境ではJPAやJDBCを利用した実装に置き換える。
 */
@Repository
public class SurveyRepositoryImpl implements ISurveyRepository {

    // 永続化の代わりにインメモリのMapを使用
    private final ConcurrentHashMap<SurveyId, Survey> surveyStore = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<SurveyId, List<SurveyAnswer>> answerStore = new ConcurrentHashMap<>();

    @Override
    public void save(Survey survey) {
        surveyStore.put(survey.getId(), survey);
        System.out.println("Saved survey: " + survey.getId().getValue());
    }

    @Override
    public void saveAnswer(SurveyAnswer surveyAnswer) {
        answerStore.computeIfAbsent(surveyAnswer.getSurveyId(), k -> new CopyOnWriteArrayList<>())
                   .add(surveyAnswer);
        System.out.println("Saved answer for survey: " + surveyAnswer.getSurveyId().getValue());
    }

    @Override
    public Optional<Survey> findById(SurveyId id) {
        return Optional.ofNullable(surveyStore.get(id));
    }

    @Override
    public List<SurveyAnswer> findAnswersBySurveyId(SurveyId surveyId) {
        return answerStore.getOrDefault(surveyId, List.of());
    }
}
