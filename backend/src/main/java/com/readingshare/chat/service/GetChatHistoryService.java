package com.readingshare.chat.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.chat.domain.model.ChatMessage;
import com.readingshare.chat.domain.repository.IChatMessageRepository;
import com.readingshare.common.exception.DatabaseAccessException;

/**
 * チャット履歴取得のアプリケーションサービス。
 * 担当: 榎本
 */
@Service
public class GetChatHistoryService {

    private final IChatMessageRepository chatMessageRepository;

    public GetChatHistoryService(IChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    /**
     * 特定の部屋のチャット履歴を取得する。
     * 
     * @param roomId 履歴を取得する部屋のID
     * @return チャットメッセージのリスト
     * @throws DatabaseAccessException データベースアクセスエラー時
     */
    @Transactional(readOnly = true)
    public List<ChatMessage> getChatHistory(UUID roomId) {
        return chatMessageRepository.findByRoomId(roomId);
    }
}
