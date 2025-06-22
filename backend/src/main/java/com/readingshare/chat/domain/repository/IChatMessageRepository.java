package com.readingshare.chat.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.readingshare.chat.domain.model.ChatMessage;

/**
 * チャットメッセージ情報の永続化を担当するリポジトリインターフェース。
 * 担当: 榎本
 */
public interface IChatMessageRepository {

    /**
     * チャットメッセージを保存する。
     * 
     * @param chatMessage 保存するチャットメッセージエンティティ
     * @return 保存されたチャットメッセージエンティティ
     */
    ChatMessage save(ChatMessage chatMessage);

    /**
     * 特定の部屋のチャット履歴を取得する。
     * 
     * @param roomId 部屋ID
     * @return チャットメッセージのリスト
     */
    List<ChatMessage> findByRoomId(UUID roomId);

    /**
     * IDでチャットメッセージを検索する。
     * 
     * @param id メッセージID
     * @return チャットメッセージが見つかった場合はOptionalにChatMessage、見つからない場合はOptional.empty()
     */
    Optional<ChatMessage> findById(UUID id);
}
