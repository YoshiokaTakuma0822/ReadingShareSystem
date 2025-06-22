package com.readingshare.chat.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readingshare.auth.infrastructure.security.UserPrincipal;
import com.readingshare.chat.domain.model.ChatMessage;
import com.readingshare.chat.dto.ChatStreamItemDto;
import com.readingshare.chat.dto.SendMessageRequest;
import com.readingshare.chat.service.GetChatHistoryService;
import com.readingshare.chat.service.SendChatMessageService;
import com.readingshare.common.exception.ApplicationException;
import com.readingshare.survey.domain.model.Survey;
import com.readingshare.survey.domain.repository.ISurveyRepository;

/**
 * グループチャットAPIのコントローラー。
 *
 * @author 02001
 * @componentId C4
 * @moduleName チャットコントローラー
 * @see SendChatMessageService
 * @see GetChatHistoryService
 * @see ISurveyRepository
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final SendChatMessageService sendChatMessageService;
    private final GetChatHistoryService getChatHistoryService;
    private final ISurveyRepository surveyRepository;

    public ChatController(SendChatMessageService sendChatMessageService,
            GetChatHistoryService getChatHistoryService,
            ISurveyRepository surveyRepository) {
        this.sendChatMessageService = sendChatMessageService;
        this.getChatHistoryService = getChatHistoryService;
        this.surveyRepository = surveyRepository;
    }

    /**
     * チャットメッセージを送信する。
     *
     * @param roomId  メッセージを送信する部屋のID
     * @param request 送信メッセージリクエスト
     * @return 送信成功時はHTTP 200 OK
     */
    @PostMapping("/{roomId}/message")
    public ResponseEntity<String> sendMessage(@PathVariable UUID roomId, @RequestBody SendMessageRequest request) {
        UUID currentUserId = getCurrentUserId();
        sendChatMessageService.sendMessage(roomId, currentUserId, request.messageContent());
        return ResponseEntity.ok("Message sent successfully.");
    }

    /**
     * チャット履歴を取得する。
     *
     * @param roomId 部屋のID
     * @return チャットメッセージのリスト
     */
    @GetMapping("/{roomId}/history")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable UUID roomId) {
        List<ChatMessage> chatHistory = getChatHistoryService.getChatHistory(roomId);
        return ResponseEntity.ok(chatHistory);
    }

    /**
     * チャット＋アンケート混在ストリームを取得する
     */
    @GetMapping("/room/{roomId}/stream")
    public ResponseEntity<List<ChatStreamItemDto>> getChatStream(@PathVariable UUID roomId) {
        List<ChatMessage> chatMessages = getChatHistoryService.getChatHistory(roomId);
        List<Survey> surveys = getSurveysByRoomId(roomId);
        List<ChatStreamItemDto> stream = new ArrayList<>();
        for (ChatMessage msg : chatMessages) {
            stream.add(new ChatStreamItemDto(msg));
        }
        for (Survey survey : surveys) {
            stream.add(new ChatStreamItemDto(survey));
        }
        // sentAt/createdAtで昇順ソート
        stream.sort((a, b) -> {
            java.time.Instant aTime = a.getType() == ChatStreamItemDto.ItemType.MESSAGE ? a.getSentAt()
                    : a.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant();
            java.time.Instant bTime = b.getType() == ChatStreamItemDto.ItemType.MESSAGE ? b.getSentAt()
                    : b.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant();
            return aTime.compareTo(bTime);
        });
        return ResponseEntity.ok(stream);
    }

    // /api/rooms/{roomId}/stream でも同じ処理を提供
    @GetMapping("/api/rooms/{roomId}/stream")
    public ResponseEntity<List<ChatStreamItemDto>> getChatStreamAlias(@PathVariable UUID roomId) {
        return getChatStream(roomId);
    }

    private List<Survey> getSurveysByRoomId(UUID roomId) {
        return surveyRepository.findByRoomId(roomId);
    }

    /**
     * 現在認証されているユーザーのIDを取得する。
     *
     * @return 現在のユーザーID
     * @throws ApplicationException 認証されていない場合
     */
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            return userPrincipal.getUserId();
        }

        throw new ApplicationException("User not authenticated");
    }
}
