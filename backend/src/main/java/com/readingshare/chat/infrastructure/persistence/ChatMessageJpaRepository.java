package com.readingshare.chat.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readingshare.chat.domain.model.ChatMessage;

/**
 * チャット情報管理部のJPAリポジトリインターフェース。
 *
 * @author 23001
 * @componentIdName C07 チャット情報管理部
 * @moduleIdName M0702 チャットメッセージJPAリポジトリ
 * @dependsOn M0704 チャットメッセージエンティティ（インフラ用）
 */
@Repository
public interface ChatMessageJpaRepository extends JpaRepository<ChatMessage, UUID> {

    /**
     * 特定の部屋のチャット履歴を取得する。
     *
     * @param roomId 部屋ID
     * @return チャットメッセージのリスト
     */
    List<ChatMessage> findByRoomIdOrderBySentAtAsc(UUID roomId);
}
