package com.readingshare.survey.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readingshare.survey.domain.model.Survey;
import com.readingshare.survey.domain.model.SurveyId;

/**
 * アンケート情報のJPAリポジトリインターフェース。
 * 担当: 成田
 */
@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {

    /**
     * アンケートIDでアンケートを検索する。
     * 
     * @param id アンケートID
     * @return アンケートが見つかった場合はOptionalにSurvey、見つからない場合はOptional.empty()
     */
    Optional<Survey> findById(SurveyId id);

    /**
     * 特定の部屋のアンケートを取得する。
     * 
     * @param roomId 部屋ID
     * @return 取得されたアンケートリスト
     */
    List<Survey> findByRoomIdOrderByCreatedAtDesc(Long roomId);
}
