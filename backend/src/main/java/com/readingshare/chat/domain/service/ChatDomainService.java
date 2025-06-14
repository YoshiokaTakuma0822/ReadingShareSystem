package com.readingshare.chat.domain.service;

import com.readingshare.chat.domain.model.ChatMessage;
import com.readingshare.chat.domain.model.UserProgress;
import com.readingshare.chat.domain.repository.IChatMessageRepository;
import com.readingshare.chat.domain.repository.IProgressRepository;
import com.readingshare.common.exception.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/**
 * チャットと読書進捗に関するドメインロジックを扱うサービス。
 * 担当: 榎本
 */
@Service
public class ChatDomainService {

    private final IChatMessageRepository chatMessageRepository;
    private final IProgressRepository progressRepository;

    public ChatDomainService(IChatMessageRepository chatMessageRepository, IProgressRepository progressRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.progressRepository = progressRepository;
    }

    /**
     * チャットメッセージを保存する。
     * @param chatMessage 保存するチャットメッセージエンティティ
     * @return 保存されたチャットメッセージエンティティ
     * @throws DomainException メッセージの保存に失敗した場合
     */
    @Transactional
    public ChatMessage sendChatMessage(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }

    /**
     * ユーザーの読書進捗を記録または更新する。
     * @param roomId 部屋ID
     * @param userId ユーザーID
     * @param currentPage 現在のページ数
     * @return 保存または更新されたUserProgressエンティティ
     * @throws DomainException 進捗の記録に失敗した場合
     */
    @Transactional
    public UserProgress recordUserProgress(Long roomId, Long userId, int currentPage) {
        Optional<UserProgress> existingProgress = progressRepository.findByRoomIdAndUserId(roomId, userId);

        UserProgress userProgress;
        if (existingProgress.isPresent()) {
            // 既存の進捗を更新
            userProgress = existingProgress.get();
            userProgress.setCurrentPage(currentPage);
            userProgress.setUpdatedAt(Instant.now());
        } else {
            // 新しい進捗を作成
            userProgress = new UserProgress(null, roomId, userId, currentPage, Instant.now());
        }
        return progressRepository.save(userProgress);
    }
}