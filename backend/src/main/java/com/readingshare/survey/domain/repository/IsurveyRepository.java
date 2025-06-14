package com.readingshare.survey.domain.repository;

import com.readingshare.survey.domain.model.Survey;
import com.readingshare.survey.domain.model.SurveyId;

import java.util.List;
import java.util.Optional;

/**
 * アンケート情報の永続化を担当するリポジトリインターフェース。
 * 担当: 成田
 */
public interface ISurveyRepository {

    /**
     * アンケートを保存する。
     * @param survey 保存するアンケートエンティティ
     * @return 保存されたアンケートエンティティ
     */
    Survey save(Survey survey);

    /**
     * アンケートIDでアンケートを検索する。
     * @param id アンケートID
     * @return アンケートが見つかった場合はOptionalにSurvey、見つからない場合はOptional.empty()
     */
    Optional<Survey> findById(SurveyId id);

    /**
     * 特定の部屋に関連する全てのアンケートを取得する。
     * @param roomId 部屋ID
     * @return アンケートのリスト
     */
    List<Survey> findByRoomId(Long roomId);

    // 必要に応じて、アンケートタイトルや作成者での検索メソッドを追加
    // Optional<Survey> findByRoomIdAndTitle(Long roomId, String title);
}