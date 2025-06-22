package com.readingshare.chat.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.auth.domain.repository.IUserRepository;
import com.readingshare.chat.domain.model.ChatMessage;
import com.readingshare.chat.domain.model.MessageContent;
import com.readingshare.chat.domain.service.ChatDomainService;
import com.readingshare.common.exception.ApplicationException;
import com.readingshare.common.exception.DatabaseAccessException;
import com.readingshare.room.domain.repository.IRoomRepository;

/**
 * チャットメッセージ送信のアプリケーションサービス。
 * 
 * @author 23001
 */
@Service
public class SendChatMessageService {

    private final ChatDomainService chatDomainService;
    private final IRoomRepository roomRepository;
    private final IUserRepository userRepository;

    public SendChatMessageService(ChatDomainService chatDomainService,
            IRoomRepository roomRepository,
            IUserRepository userRepository) {
        this.chatDomainService = chatDomainService;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    /**
     * チャットメッセージを送信する。
     *
     * @param roomId  送信する部屋のID
     * @param userId  送信するユーザーのID
     * @param content メッセージ内容
     * @return 送信されたチャットメッセージ
     * @throws ApplicationException    部屋またはユーザーが存在しない場合、またはメッセージが無効な場合
     * @throws DatabaseAccessException データベースアクセスエラー時
     */
    @Transactional
    public ChatMessage sendMessage(UUID roomId, UUID userId, String content) {
        // 入力値の検証
        validateInput(roomId, userId, content);

        // ユーザーが部屋に参加しているかチェック
        validateUserInRoom(roomId, userId);

        // メッセージ内容の検証
        MessageContent messageContent = validateAndCreateMessageContent(content);

        // チャットメッセージの作成と送信
        ChatMessage chatMessage = new ChatMessage(roomId, userId, messageContent, Instant.now());
        chatDomainService.sendChatMessage(chatMessage);

        return chatMessage;
    }

    /**
     * 匿名ユーザーのチャットメッセージを送信する。
     *
     * @param roomId      送信する部屋のID
     * @param anonymousId 匿名ユーザーのID
     * @param content     メッセージ内容
     * @return 送信されたチャットメッセージ
     * @throws ApplicationException    部屋が存在しない場合、またはメッセージが無効な場合
     * @throws DatabaseAccessException データベースアクセスエラー時
     */
    @Transactional
    public ChatMessage sendAnonymousMessage(UUID roomId, String anonymousId, String content) {
        // 入力値の検証
        if (roomId == null) {
            throw new ApplicationException("Room ID cannot be null");
        }
        if (anonymousId == null || anonymousId.trim().isEmpty()) {
            throw new ApplicationException("Anonymous ID cannot be null or empty");
        }

        // 部屋の存在チェック
        if (!roomRepository.findById(roomId).isPresent()) {
            throw new ApplicationException("Room not found. Room ID: " + roomId);
        }

        // メッセージ内容の検証
        MessageContent messageContent = validateAndCreateMessageContent(content);

        // 匿名ユーザーの場合はnullをuserIdとして使用
        ChatMessage chatMessage = new ChatMessage(roomId, null, messageContent, Instant.now());
        chatDomainService.sendChatMessage(chatMessage);

        return chatMessage;
    }

    /**
     * 入力値の基本検証を行う。
     */
    private void validateInput(UUID roomId, UUID userId, String content) {
        if (roomId == null) {
            throw new ApplicationException("Room ID cannot be null");
        }
        if (userId == null) {
            throw new ApplicationException("User ID cannot be null");
        }
        if (content == null) {
            throw new ApplicationException("Message content cannot be null");
        }
    }

    /**
     * ユーザーが部屋に参加しているかを検証する。
     */
    private void validateUserInRoom(UUID roomId, UUID userId) {
        // 部屋の存在チェック
        if (!roomRepository.findById(roomId).isPresent()) {
            throw new ApplicationException("Room not found. Room ID: " + roomId);
        }

        // ユーザーの存在チェック
        if (!userRepository.findById(userId).isPresent()) {
            throw new ApplicationException("User not found. User ID: " + userId);
        }

        // TODO: ユーザーが部屋に参加しているかのチェックを実装
        // 現在は部屋とユーザーの存在チェックのみ
    }

    /**
     * メッセージ内容を検証し、MessageContentオブジェクトを作成する。
     */
    private MessageContent validateAndCreateMessageContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new ApplicationException("Message content cannot be empty");
        }

        // メッセージの長さ制限チェック
        if (content.length() > 1000) {
            throw new ApplicationException("Message content is too long. Maximum length is 1000 characters");
        }

        // 不適切な内容のフィルタリング（基本的なもの）
        String filteredContent = filterInappropriateContent(content.trim());

        return new MessageContent(filteredContent);
    }

    /**
     * 不適切な内容の基本的なフィルタリングを行う。
     */
    private String filterInappropriateContent(String content) {
        // TODO: より高度なコンテンツフィルタリングを実装
        // 現在は基本的なHTML/スクリプトタグの除去のみ
        return content.replaceAll("<[^>]*>", "")
                .replaceAll("script", "")
                .replaceAll("javascript:", "");
    }
}
