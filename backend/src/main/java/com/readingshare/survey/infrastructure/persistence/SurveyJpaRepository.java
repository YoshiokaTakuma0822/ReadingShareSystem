package com.readingshare.survey.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readingshare.survey.domain.model.Survey;

/**
 * SurveyJpaRepository は、アンケート情報のJPAリポジトリインターフェースです。
 * アンケートの検索や保存を行います。
 *
 * @author 23002
 */
@Repository
public interface SurveyJpaRepository extends JpaRepository<Survey, UUID> {

    /**
     * アンケートIDでアンケートを検索する。
     *
     * @param id アンケートID
     * @return アンケートが見つかった場合はOptionalにSurvey、見つからない場合はOptional.empty()
     */
    @Override
    Optional<Survey> findById(UUID id);

    /**
     * 部屋IDでアンケートを取得します。
     *
     * @param roomId 部屋ID
     * @return 取得されたアンケートリスト
     */
    List<Survey> findByRoomIdOrderByCreatedAtDesc(UUID roomId);
}
