package com.readingshare.survey.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.readingshare.chat.domain.model.ChatMessage;
import com.readingshare.chat.domain.model.MessageContent;
import com.readingshare.chat.domain.repository.IChatMessageRepository;
import com.readingshare.chat.service.ChatMessageBroadcastService;
import com.readingshare.room.domain.repository.IRoomRepository;
import com.readingshare.survey.domain.model.Survey;

/**
 * アンケート作成時にチャットメッセージを自動送信するサービス
 */
@Service
public class SurveyNotificationService {

    private final IChatMessageRepository chatMessageRepository;
    private final IRoomRepository roomRepository;
    private final ChatMessageBroadcastService chatMessageBroadcastService;

    public SurveyNotificationService(IChatMessageRepository chatMessageRepository,
            IRoomRepository roomRepository,
            ChatMessageBroadcastService chatMessageBroadcastService) {
        this.chatMessageRepository = chatMessageRepository;
        this.roomRepository = roomRepository;
        this.chatMessageBroadcastService = chatMessageBroadcastService;
    }

    /**
     * アンケート作成通知をチャットメッセージとして送信
     *
     * @param survey        作成されたアンケート
     * @param creatorUserId アンケート作成者のユーザーID
     */
    public void sendSurveyCreatedNotification(Survey survey, UUID creatorUserId) {
        // 部屋を取得
        var room = roomRepository.findById(survey.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found: " + survey.getRoomId()));

        // アンケート作成通知のメッセージ内容（シンプルな通知メッセージ）
        String notificationContent = "survey-created";
        MessageContent messageContent = new MessageContent(notificationContent);

        // アンケートメッセージを作成
        ChatMessage surveyMessage = new ChatMessage(room, creatorUserId, messageContent, Instant.now(), survey.getId());

        // メッセージを保存
        ChatMessage savedMessage = chatMessageRepository.save(surveyMessage);

        // WebSocketでブロードキャスト
        chatMessageBroadcastService.broadcastToRoom(survey.getRoomId().toString(), savedMessage);
    }
}
