package com.readingshare.room.infrastructure.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RoomEndNotificationScheduler {
    // シンプルな状態のため、フィールドやロジックなし

    @Scheduled(cron = "0 * * * * *")
    public void notifyRoomEndSoon() {
        // endTimeによる通知ロジックは廃止
    }
}
