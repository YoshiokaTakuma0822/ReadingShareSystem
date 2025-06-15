package com.readingshare.chat.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.readingshare.chat.domain.service.ChatDomainService;
import com.readingshare.common.exception.ApplicationException;

/**
 * 読書進捗記録のアプリケーションサービス。
 * 担当: 榎本
 */
@Service
public class RecordProgressService {

    private final ChatDomainService chatDomainService;

    public RecordProgressService(ChatDomainService chatDomainService) {
        this.chatDomainService = chatDomainService;
    }

    /**
     * ユーザーの読書進捗を記録または更新する。
     * 
     * @param roomId      部屋ID
     * @param userId      ユーザーID
     * @param currentPage 現在のページ数
     * @throws ApplicationException 進捗記録に失敗した場合
     */
    public void recordProgress(UUID roomId, UUID userId, int currentPage) {
        chatDomainService.recordUserProgress(roomId, userId, currentPage);
    }
}
