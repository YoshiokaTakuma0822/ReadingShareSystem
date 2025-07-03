package com.readingshare.chat.websocket;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readingshare.chat.dto.ChatMessageDto;

/**
 * 通知専用のネイティブWebSocketハンドラー
 */
public class NotificationWebSocketHandler extends TextWebSocketHandler {
    // roomId -> sessions
    private static final ConcurrentHashMap<String, Set<WebSocketSession>> sessionsMap = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String path = session.getUri().getPath();
        // /ws/chat/notifications/{roomId}
        String roomId = path.substring(path.lastIndexOf('/') + 1);
        sessionsMap.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status)
            throws Exception {
        String path = session.getUri().getPath();
        String roomId = path.substring(path.lastIndexOf('/') + 1);
        Set<WebSocketSession> sessions = sessionsMap.get(roomId);
        if (sessions != null)
            sessions.remove(session);
    }

    /**
     * roomId 宛に新着チャット通知を送信
     */
    public void broadcastMessage(String roomId, String sentAt) {
        Map<String, String> notification = Map.of(
                "roomId", roomId,
                "event", "newMessage",
                "sentAt", sentAt);
        Set<WebSocketSession> sessions = sessionsMap.get(roomId);
        if (sessions == null)
            return;
        try {
            String payload = mapper.writeValueAsString(notification);
            TextMessage msg = new TextMessage(payload);
            sessions.forEach(s -> {
                try {
                    s.sendMessage(msg);
                } catch (Exception ignored) {
                }
            });
        } catch (Exception ignored) {
            // ignore serialization/send errors
        }
    }

    /**
     * roomId 宛に進捗更新を通知
     */
    public void broadcastProgress(String roomId, int percent, String detail) {
        // Include userId and currentPage in payload
        Map<String, Object> payloadMap = Map.of(
                "roomId", roomId,
                "event", "progressUpdate",
                "percent", percent,
                "userId", detail,
                "currentPage", detail);
        Set<WebSocketSession> sessions = sessionsMap.get(roomId);
        if (sessions == null)
            return;
        try {
            String json = mapper.writeValueAsString(payloadMap);
            TextMessage msg = new TextMessage(json);
            sessions.forEach(s -> {
                try {
                    s.sendMessage(msg);
                } catch (Exception ignored) {
                }
            });
        } catch (Exception ignored) {
            // ignore serialization/send errors
        }
    }

    /**
     * roomId 宛にチャットメッセージをブロードキャスト
     */
    public void broadcastChatMessage(String roomId, ChatMessageDto chatMessage) {
        Set<WebSocketSession> sessions = sessionsMap.get(roomId);
        if (sessions == null)
            return;
        try {
            // チャットメッセージをJSONに変換
            String json = mapper.writeValueAsString(chatMessage);
            TextMessage msg = new TextMessage(json);
            sessions.forEach(s -> {
                try {
                    s.sendMessage(msg);
                } catch (Exception ignored) {
                    // セッションエラーは無視
                }
            });
        } catch (Exception ignored) {
            // シリアライゼーションエラーは無視
        }
    }
}
