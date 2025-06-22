package com.readingshare.room.infrastructure.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RoomAutoDeleteScheduler {
    // シンプルな状態のため、フィールドやロジックなし

    @Scheduled(cron = "0 * * * * *")
    public void deleteExpiredRooms() {
        // endTimeによる自動削除ロジックは廃止
    }
}
