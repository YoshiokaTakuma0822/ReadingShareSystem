package com.readingshare.room.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.readingshare.room.domain.model.Member;
import com.readingshare.room.dto.MemberResponse;
import com.readingshare.room.event.MemberUpdateEvent;

@Service
public class ActiveMembersService {
    private static final Logger logger = LoggerFactory.getLogger(ActiveMembersService.class);
    private static final int TIMEOUT_MINUTES = 5;

    // thread-safe map for userId to User mapping
    private final ConcurrentHashMap<Long, MemberResponse> activeUsers = new ConcurrentHashMap<>();
    // thread-safe map for userId to last activity time
    private final ConcurrentHashMap<Long, LocalDateTime> lastActivityTimes = new ConcurrentHashMap<>();

    private final ApplicationEventPublisher eventPublisher;

    public ActiveMembersService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void addMember(MemberResponse member) {
        if (member != null && member.id() != null) {
            boolean wasAlreadyActive = activeUsers.containsKey(member.id());
            activeUsers.put(member.id(), member);
            updateMemberTimeout(member.id());

            if (wasAlreadyActive) {
                logger.debug("User {} ({}) was already active, updated timeout", member.id(), member.name());
            } else {
                logger.info("Added new active user {} ({})", member.id(), member.name());
            }
        }
    }

    public void addMember(Member member) {
        if (member != null && member.getId() != null) {
            // Convert domain model to DTO in service layer
            MemberResponse memberResponse = new MemberResponse(
                    member.getId(),
                    member.getName(),
                    member.getRoom() != null ? member.getRoom().getId() : null);
            addMember(memberResponse);
        }
    }

    public void removeUser(MemberResponse user) {
        if (user != null && user.id() != null) {
            activeUsers.remove(user.id());
            lastActivityTimes.remove(user.id());
        }
    }

    public void removeUser(Member user) {
        if (user != null && user.getId() != null) {
            activeUsers.remove(user.getId());
            lastActivityTimes.remove(user.getId());
        }
    }

    public void updateMemberTimeout(Long userId) {
        if (userId != null && activeUsers.containsKey(userId)) {
            lastActivityTimes.put(userId, LocalDateTime.now());
            logger.debug("Updated last activity time for user {}", userId);
        }
    }

    public List<MemberResponse> getActiveUsers() {
        return activeUsers.values().stream()
                .collect(Collectors.toList());
    }

    /**
     * 定期的にタイムアウトしたユーザーをクリーンアップする
     * 5分間アクティビティがないユーザーを削除
     */
    @Scheduled(fixedRate = 60000) // 1分ごとに実行
    public void cleanupInactiveUsers() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(TIMEOUT_MINUTES);

        List<Long> inactiveUserIds = lastActivityTimes.entrySet().stream()
                .filter(entry -> entry.getValue().isBefore(cutoffTime))
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());

        for (Long userId : inactiveUserIds) {
            var user = activeUsers.get(userId);
            if (user != null) {
                removeUser(user);
                eventPublisher.publishEvent(new MemberUpdateEvent(this));
            }
        }
    }
}
