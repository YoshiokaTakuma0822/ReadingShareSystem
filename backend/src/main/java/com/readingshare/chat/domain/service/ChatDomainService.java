package com.readingshare.chat.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.chat.domain.model.ChatMessage;
import com.readingshare.chat.domain.repository.IChatMessageRepository;
import com.readingshare.common.exception.DomainException;

/**
 * グループチャットのドメインサービス。
 *
 * @author 23001
 * @componentIdName C04 グループチャット
 * @moduleIdName M0403 チャットドメインサービス
 */
@Service
public class ChatDomainService {

    private final IChatMessageRepository chatMessageRepository;

    public ChatDomainService(IChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    /**
     * チャットメッセージを保存する。
     *
     * @param chatMessage 保存するチャットメッセージエンティティ
     * @return 保存されたチャットメッセージエンティティ
     * @throws DomainException メッセージの保存に失敗した場合
     */
    @Transactional
    public ChatMessage sendChatMessage(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }
}
