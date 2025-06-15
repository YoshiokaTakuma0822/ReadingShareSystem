package com.readingshare.chat.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readingshare.chat.domain.model.ChatMessage;

/**
 * チャットメッセージ情報のJPAリポジトリインターフェース。
 * 担当: 榎本
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    /**
     * 特定の部屋のチャット履歴を取得する。
     *
     * @param roomId 部屋ID
     * @return チャットメッセージのリスト
     */
    List<ChatMessage> findByRoomIdOrderBySentAtAsc(UUID roomId);
}
