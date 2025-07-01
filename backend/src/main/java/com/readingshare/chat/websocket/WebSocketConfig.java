package com.readingshare.chat.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // ネイティブWebSocketハンドラー登録（通知専用）
        registry.addHandler(notificationHandler(), "/ws/chat/notifications/*")
                .setAllowedOrigins("*");
    }

    @Bean
    NotificationWebSocketHandler notificationHandler() {
        return new NotificationWebSocketHandler();
    }
}
