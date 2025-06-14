package com.readingshare.chat.service;

import com.readingshare.chat.domain.model.ChatMessage;
import com.readingshare.chat.domain.model.MessageContent;
import com.readingshare.chat.domain.service.ChatDomainService;
import com.readingshare.common.exception.ApplicationException;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * チャットメッセージ送信のアプリケーションサービス。
 * 担当: 榎本
 */
@Service
public class SendChatMessageService {

    private final ChatDomainService chatDomainService;

    public SendChatMessageService(ChatDomainService chatDomainService) {
        this.chatDomainService = chatDomainService;
    }

    /**
     * チャットメッセージを送信する。
     * @param roomId 送信する部屋のID
     * @param userId 送信するユーザーのID
     * @param content メッセージ内容
     * @throws ApplicationException メッセージ送信に失敗した場合
     */
    public void sendMessage(Long roomId, Long userId, String content) {
        ChatMessage chatMessage = new ChatMessage(null, roomId, userId, new MessageContent(content), Instant.now());
        chatDomainService.sendChatMessage(chatMessage);
    }
}