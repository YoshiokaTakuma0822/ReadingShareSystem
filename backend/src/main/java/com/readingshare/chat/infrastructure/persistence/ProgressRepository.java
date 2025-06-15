package com.readingshare.chat.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readingshare.chat.domain.model.UserProgress;

/**
 * 読書進捗情報のJPAリポジトリインターフェース。
 * 担当: 榎本
 */
@Repository
public interface ProgressRepository extends JpaRepository<UserProgress, Long> {

    /**
     * 特定の部屋の全ユーザーの進捗情報を取得する。
     * 
     * @param roomId 部屋ID
     * @return 取得されたユーザー進捗リスト
     */
    List<UserProgress> findByRoomIdOrderByUserIdAsc(Long roomId);

    /**
     * 特定の部屋とユーザーの組み合わせで進捗情報を検索する。
     * 
     * @param roomId 部屋ID
     * @param userId ユーザーID
     * @return 進捗情報が見つかった場合はOptionalにUserProgress、見つからない場合はOptional.empty()
     */
    Optional<UserProgress> findByRoomIdAndUserId(Long roomId, Long userId);
}
