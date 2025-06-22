package com.readingshare.state.domain.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RoomReadingStateは、特定の部屋における読書の状態を表します。
 * このクラスは、部屋IDとその部屋にいるユーザーの読書状態を管理します。
 *
 * @author 02003
 * @componentId C4
 * @moduleName 部屋読書状態モデル
 */
public class RoomReadingState {
    private final String roomId;
    private final Map<String, UserReadingState> userStates;

    /**
     * RoomReadingStateのコンストラクタです。
     *
     * @param roomId 部屋の一意な識別子
     */
    public RoomReadingState(String roomId) {
        this.roomId = roomId;
        this.userStates = new HashMap<>();
    }

    /**
     * 部屋IDを取得します。
     *
     * @return 部屋ID
     */
    public String getRoomId() {
        return roomId;
    }

    /**
     * ユーザーの読書状態のマップを取得します。
     *
     * @return ユーザーIDとUserReadingStateのマップ
     */
    public Map<String, UserReadingState> getUserStates() {
        return userStates;
    }

    /**
     * ユーザーの読書状態を更新します。
     *
     * @param state 更新するユーザーの読書状態
     */
    public void updateUserState(UserReadingState state) {
        userStates.put(state.getUserId(), state);
    }

    /**
     * 特定のユーザーIDに対応するユーザーの読書状態を取得します。
     *
     * @param userId ユーザーの一意な識別子
     * @return 該当ユーザーのUserReadingState、存在しない場合はnull
     */
    public UserReadingState getUserState(String userId) {
        return userStates.get(userId);
    }

    /**
     * 全てのユーザーの読書状態を取得します。
     *
     * @return UserReadingStateのリスト
     */
    public List<UserReadingState> getAllUserStates() {
        return List.copyOf(userStates.values());
    }
}
