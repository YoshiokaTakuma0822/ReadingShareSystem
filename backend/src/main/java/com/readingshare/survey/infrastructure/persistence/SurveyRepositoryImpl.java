package com.readingshare.survey.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.readingshare.survey.domain.model.Survey;
import com.readingshare.survey.domain.model.SurveyAnswer;
import com.readingshare.survey.domain.repository.ISurveyRepository;

/**
 * SurveyRepositoryImpl は、アンケート情報のリポジトリ実装クラスです。
 *
 * @author 02002
 * @componentId C8
 * @moduleName アンケートリポジトリ実装
 * @see SurveyJpaRepository
 * @see SurveyAnswerJpaRepository
 */
@Repository
public class SurveyRepositoryImpl implements ISurveyRepository {
    @Autowired
    private SurveyJpaRepository surveyRepository;

    @Autowired
    private SurveyAnswerJpaRepository surveyAnswerRepository;

    /**
     * アンケートを保存します。
     *
     * @param survey 保存するアンケート
     * @return 保存されたアンケート
     */
    @Override
    public Survey save(Survey survey) {
        return surveyRepository.save(survey);
    }

    /**
     * アンケートの回答を保存します。
     *
     * @param answer 保存するアンケート回答
     * @return 保存されたアンケート回答
     */
    @Override
    public SurveyAnswer saveAnswer(SurveyAnswer answer) {
        return surveyAnswerRepository.save(answer);
    }

    /**
     * 指定されたIDのアンケートを取得します。
     *
     * @param id アンケートID
     * @return 指定されたIDのアンケートを含むOptional、存在しない場合は空
     */
    @Override
    public Optional<Survey> findById(UUID id) {
        return surveyRepository.findById(id);
    }

    /**
     * 指定された部屋IDに紐づくアンケート一覧を取得します。
     * 作成日時の降順でソートされます。
     *
     * @param roomId 部屋ID
     * @return アンケートリスト
     */
    @Override
    public List<Survey> findByRoomId(UUID roomId) {
        return surveyRepository.findByRoomIdOrderByCreatedAtDesc(roomId);
    }

    /**
     * 指定されたアンケートIDの回答一覧を取得します。
     *
     * @param surveyId アンケートID
     * @return アンケート回答リスト
     */
    @Override
    public List<SurveyAnswer> findAnswersBySurveyId(UUID surveyId) {
        return surveyAnswerRepository.findBySurveyId(surveyId);
    }

    /**
     * 指定されたアンケートIDおよびユーザーIDに一致する回答を取得します。
     *
     * @param surveyId アンケートID
     * @param userId   ユーザーID
     * @return 該当する回答(Optional)
     */
    @Override
    public Optional<SurveyAnswer> findBySurveyIdAndUserId(UUID surveyId, UUID userId) {
        return surveyAnswerRepository.findBySurveyIdAndUserId(surveyId, userId);
    }
}
