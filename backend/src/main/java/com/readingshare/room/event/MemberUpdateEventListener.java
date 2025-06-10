package com.readingshare.room.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.readingshare.common.service.WebSocketService;

/**
 * Event listener that handles user update events.
 * This component listens for UserUpdateEvents and triggers WebSocket
 * notifications.
 */
@Component
public class MemberUpdateEventListener {
    private final WebSocketService webSocketService;

    public MemberUpdateEventListener(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @EventListener
    public void handleUserUpdate(MemberUpdateEvent event) {
        webSocketService.notifyUsersUpdate();
    }
}
