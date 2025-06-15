package com.readingshare.chat.domain.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.chat.domain.model.ChatMessage;
import com.readingshare.chat.domain.model.UserProgress;
import com.readingshare.chat.domain.repository.IChatMessageRepository;
import com.readingshare.chat.domain.repository.IProgressRepository;
import com.readingshare.common.exception.DomainException;

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
     * 
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
     * 
     * @param roomId      部屋ID
     * @param userId      ユーザーID
     * @param currentPage 現在のページ数
     * @return 保存または更新されたUserProgressエンティティ
     * @throws DomainException 進捗の記録に失敗した場合
     */
    @Transactional
    public UserProgress recordUserProgress(UUID roomId, UUID userId, int currentPage) {
        Optional<UserProgress> existingProgress = progressRepository.findByRoomIdAndUserId(roomId, userId);

        if (existingProgress.isPresent()) {
            // 既存の進捗情報を更新
            UserProgress progress = existingProgress.get();
            progress.setCurrentPage(currentPage);
            progress.setUpdatedAt(Instant.now());
            return progressRepository.save(progress);
        } else {
            // 新しい進捗情報を作成
            UserProgress progress = new UserProgress(roomId, userId, currentPage, Instant.now());
            return progressRepository.save(progress);
        }
    }
}
