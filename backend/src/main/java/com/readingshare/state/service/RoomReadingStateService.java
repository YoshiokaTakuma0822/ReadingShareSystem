package com.readingshare.state.service;

import org.springframework.stereotype.Service;

import com.readingshare.state.domain.model.RoomReadingState;
import com.readingshare.state.domain.model.UserReadingState;
import com.readingshare.state.domain.repository.RoomReadingStateRepository;

/**
 * RoomReadingStateService は、部屋の読書状態を管理するためのサービスクラスです。
 * ユーザーの読書状態を更新したり、部屋の読書状態を取得する機能を提供します。
 *
 * @author 02003
 * @componentId C4
 * @moduleName 部屋読書状態サービス
 */
@Service
public class RoomReadingStateService {
    private final RoomReadingStateRepository repository;

    public RoomReadingStateService(RoomReadingStateRepository repository) {
        this.repository = repository;
    }

    /**
     * ユーザーの読書状態を更新します。
     *
     * @param roomId    更新対象の部屋ID
     * @param userState 更新するユーザーの読書状態
     */
    public void updateUserReadingState(String roomId, UserReadingState userState) {
        RoomReadingState roomState = repository.findByRoomId(roomId);
        if (roomState == null) {
            roomState = new RoomReadingState(roomId);
        }
        roomState.updateUserState(userState);
        repository.save(roomState);
    }

    /**
     * 部屋の読書状態を取得します。
     *
     * @param roomId 取得対象の部屋ID
     * @return 部屋の読書状態
     */
    public RoomReadingState getRoomReadingState(String roomId) {
        return repository.findByRoomId(roomId);
    }
}
