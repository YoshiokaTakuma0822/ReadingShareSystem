package com.readingshare.common.service;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.readingshare.chat.service.MessageService;
import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.repository.MemberRepository;
import com.readingshare.room.event.MemberUpdateEvent;
import com.readingshare.room.service.ActiveMembersService;

/**
 * Centralized service for WebSocket functionality.
 * Handles session management and message broadcasting.
 */
@Service
public class WebSocketService {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketService.class);
    private static final String WS_MESSAGE_TOPIC = "/topic/chat.messages.update";
    private static final String WS_USERS_TOPIC = "/topic/chat.users.update";

    // thread-safe map for sessionId to userId mapping
    private final ConcurrentHashMap<String, Long> sessionUserMap = new ConcurrentHashMap<>();

    private final SimpMessageSendingOperations messagingTemplate;
    private final MemberRepository memberRepository;
    private final ActiveMembersService activeUsersService;
    private final MessageService messageService;
    private final ApplicationEventPublisher eventPublisher;

    public WebSocketService(
            SimpMessageSendingOperations messagingTemplate,
            MemberRepository memberRepository,
            ActiveMembersService activeUsersService,
            MessageService messageService,
            ApplicationEventPublisher eventPublisher) {
        this.messagingTemplate = messagingTemplate;
        this.memberRepository = memberRepository;
        this.activeUsersService = activeUsersService;
        this.messageService = messageService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Handles user session connection.
     */
    public void handleUserConnect(String sessionId, Long userId) {
        sessionUserMap.put(sessionId, userId);
        memberRepository.findById(userId).ifPresentOrElse(member -> {
            activeUsersService.addMember(member);
            eventPublisher.publishEvent(new MemberUpdateEvent(this));
            logger.info("Added user {} to active list", userId);

            // Create join message for the member's room
            Room memberRoom = member.getRoom();
            if (memberRoom != null) {
                messageService.createJoinMessage(member, memberRoom);
                broadcastMessage();
                broadcastMessageToRoom(memberRoom.getId());
                logger.debug("Created join message for user {} in room {}", member.getName(), memberRoom.getId());
            }
        }, () -> {
            logger.warn("User not found for ID: {}", userId);
        });
    }

    /**
     * Handles user session disconnect.
     */
    public void handleUserDisconnect(String sessionId) {
        Long userId = sessionUserMap.remove(sessionId);
        if (userId != null) {
            logger.info("User disconnected: {}", userId);

            memberRepository.findById(userId).ifPresentOrElse(member -> {
                // Create leave message for the member's room
                Room memberRoom = member.getRoom();
                if (memberRoom != null) {
                    messageService.createLeaveMessage(member, memberRoom);
                    broadcastMessage();
                    broadcastMessageToRoom(memberRoom.getId());
                    logger.debug("Created leave message for user {} in room {}", member.getName(), memberRoom.getId());
                }

                activeUsersService.removeUser(member);
                eventPublisher.publishEvent(new MemberUpdateEvent(this));
            }, () -> {
                logger.warn("User not found for ID: {}", userId);
            });
        }
    }

    /**
     * Updates user activity timeout on subscription.
     */
    public void handleUserSubscribe(String sessionId, String destination) {
        Long userId = sessionUserMap.get(sessionId);
        if (userId != null) {
            logger.debug("User {} subscribed to {}", userId, destination);
            activeUsersService.updateMemberTimeout(userId);
        }
    }

    /**
     * Sends an update signal to all clients that new messages are available.
     * Clients should fetch the latest messages via REST API upon receiving this
     * notification.
     * The payload is intentionally empty as clients only use this as a trigger.
     */
    public void broadcastMessage() {
        messagingTemplate.convertAndSend(WS_MESSAGE_TOPIC, "");
        logger.debug("Broadcasted message update notification");
    }

    /**
     * Sends an update signal to clients subscribed to a specific room that new
     * messages are available.
     * Clients should fetch the latest messages via REST API upon receiving this
     * notification.
     * The payload is intentionally empty as clients only use this as a trigger.
     *
     * @param roomId the ID of the room to broadcast to
     */
    public void broadcastMessageToRoom(Long roomId) {
        String roomTopic = "/topic/chat.room." + roomId + ".messages.update";
        messagingTemplate.convertAndSend(roomTopic, "");
        logger.debug("Broadcasted message update notification to room {}", roomId);
    }

    /**
     * Sends an update signal to all clients that the user list has changed.
     * Clients should fetch the latest active users via REST API upon receiving this
     * notification.
     * The payload is intentionally empty as clients only use this as a trigger.
     */
    public void notifyUsersUpdate() {
        messagingTemplate.convertAndSend(WS_USERS_TOPIC, "");
        logger.debug("Broadcasted users update notification");
    }
}
