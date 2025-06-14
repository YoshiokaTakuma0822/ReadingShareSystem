package com.readingshare.state.service;

import org.springframework.stereotype.Service;

import com.readingshare.state.domain.model.RoomReadingState;
import com.readingshare.state.domain.model.UserReadingState;
import com.readingshare.state.domain.repository.RoomReadingStateRepository;

@Service
public class RoomReadingStateService {
    private final RoomReadingStateRepository repository;
    private static final int INTERVAL_STEP = 3;
    private static final int MIN_INTERVAL = 10;
    private static final int MAX_INTERVAL = 300;

    public RoomReadingStateService(RoomReadingStateRepository repository) {
        this.repository = repository;
    }

    public void updateUserReadingState(String roomId, UserReadingState userState) {
        RoomReadingState roomState = repository.findByRoomId(roomId);
        if (roomState == null) {
            roomState = new RoomReadingState(roomId);
        }
        roomState.updateUserState(userState);
        repository.save(roomState);
    }

    public RoomReadingState getRoomReadingState(String roomId) {
        return repository.findByRoomId(roomId);
    }

    // ページを進める/戻す + インターバル調整
    public UserReadingState turnPage(String roomId, String userId, String direction) {
        RoomReadingState roomState = repository.findByRoomId(roomId);
        if (roomState == null)
            return null;
        UserReadingState user = roomState.getUserState(userId);
        if (user == null)
            return null;
        int page = user.getCurrentPage();
        int interval = user.getAutoTurnIntervalSec();
        if ("next".equals(direction)) {
            page++;
            interval = Math.max(MIN_INTERVAL, interval - INTERVAL_STEP);
        } else if ("prev".equals(direction)) {
            page = Math.max(0, page - 1);
            interval = Math.min(MAX_INTERVAL, interval + INTERVAL_STEP);
        }
        UserReadingState updated = new UserReadingState(
                user.getUserId(), user.getUserName(), user.getIconUrl(), page, user.getComment(), interval);
        roomState.updateUserState(updated);
        repository.save(roomState);
        return updated;
    }

    // 自動でページを進める
    public UserReadingState autoTurnPage(String roomId, String userId) {
        RoomReadingState roomState = repository.findByRoomId(roomId);
        if (roomState == null)
            return null;
        UserReadingState user = roomState.getUserState(userId);
        if (user == null)
            return null;
        int page = user.getCurrentPage() + 1;
        UserReadingState updated = new UserReadingState(
                user.getUserId(), user.getUserName(), user.getIconUrl(), page, user.getComment(),
                user.getAutoTurnIntervalSec());
        roomState.updateUserState(updated);
        repository.save(roomState);
        return updated;
    }
}
