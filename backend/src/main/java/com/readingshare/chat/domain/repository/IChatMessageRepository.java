package com.readingshare.chat.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.readingshare.chat.domain.model.ChatMessage;
import com.readingshare.room.domain.model.Room;

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
     * @param room 部屋エンティティ
     * @return チャットメッセージのリスト
     */
    List<ChatMessage> findByRoom(Room room);

    /**
     * IDでチャットメッセージを検索する。
     * 
     * @param id メッセージID
     * @return チャットメッセージが見つかった場合はOptionalにChatMessage、見つからない場合はOptional.empty()
     */
    Optional<ChatMessage> findById(UUID id);

    /**
     * チャットメッセージを削除する。
     * 
     * @param chatMessage 削除するチャットメッセージエンティティ
     */
    void delete(ChatMessage chatMessage);
}
