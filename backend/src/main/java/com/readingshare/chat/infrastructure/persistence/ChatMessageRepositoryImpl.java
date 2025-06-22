package com.readingshare.chat.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.readingshare.chat.domain.model.ChatMessage;
import com.readingshare.chat.domain.repository.IChatMessageRepository;

/**
 * チャット情報管理部のチャットメッセージリポジトリ実装。
 *
 * @author 23001
 * @componentIdName C07 チャット情報管理部
 * @moduleIdName M0701 チャットメッセージリポジトリ実装
 * @dependsOn M0702 チャットメッセージJPAリポジトリ
 * @dependsOn M0704 チャットメッセージエンティティ（インフラ用）
 */
@Repository
public class ChatMessageRepositoryImpl implements IChatMessageRepository {

    @Autowired
    @Lazy
    private ChatMessageJpaRepository chatMessageRepository;

    /**
     * チャットメッセージを保存します。
     *
     * @param chatMessage 保存するチャットメッセージ
     * @return 保存されたチャットメッセージ
     */
    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }

    /**
     * 指定された部屋IDに関連付けられたチャットメッセージを取得します。
     *
     * @param roomId 部屋ID
     * @return 指定された部屋IDに関連付けられたチャットメッセージのリスト
     */
    @Override
    public List<ChatMessage> findByRoomId(UUID roomId) {
        return chatMessageRepository.findByRoomIdOrderBySentAtAsc(roomId);
    }

    /**
     * 指定されたIDのチャットメッセージを取得します。
     *
     * @param id チャットメッセージのID
     * @return 指定されたIDのチャットメッセージを含むOptional、見つからない場合は空のOptional
     */
    @Override
    public Optional<ChatMessage> findById(UUID id) {
        return chatMessageRepository.findById(id);
    }
}
