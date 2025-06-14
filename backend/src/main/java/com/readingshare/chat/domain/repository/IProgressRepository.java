package com.readingshare.chat.domain.repository;

<<<<<<< Updated upstream
import com.readingshare.chat.domain.model.UserProgress;

import java.util.List;
import java.util.Optional;

/**
 * 読書進捗情報の永続化を担当するリポジトリインターフェース。
 * 担当: 榎本
 */
public interface IProgressRepository {

    /**
     * ユーザーの読書進捗を保存する。
     * @param userProgress 保存するユーザー進捗エンティティ
     * @return 保存されたユーザー進捗エンティティ
     */
    UserProgress save(UserProgress userProgress);

    /**
     * 特定の部屋の全ユーザーの進捗情報を取得する。
     * @param roomId 部屋ID
     * @return 取得されたユーザー進捗リスト
     */
    List<UserProgress> findByRoomId(Long roomId);

    /**
     * 特定の部屋とユーザーの組み合わせで進捗情報を検索する。
     * @param roomId 部屋ID
     * @param userId ユーザーID
     * @return 進捗情報が見つかった場合はOptionalにUserProgress、見つからない場合はOptional.empty()
     */
    Optional<UserProgress> findByRoomIdAndUserId(Long roomId, Long userId);
}
