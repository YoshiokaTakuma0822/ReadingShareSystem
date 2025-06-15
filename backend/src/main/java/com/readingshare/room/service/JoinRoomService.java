package com.readingshare.room.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.readingshare.common.exception.ApplicationException;
import com.readingshare.room.domain.service.RoomDomainService;

/**
 * 部屋参加のアプリケーションサービス。
 * 担当: 芳岡
 */
@Service
public class JoinRoomService {

    private final RoomDomainService roomDomainService;

    public JoinRoomService(RoomDomainService roomDomainService) {
        this.roomDomainService = roomDomainService;
    }

    /**
     * ユーザーを部屋に参加させる。
     *
     * @param roomId       参加する部屋のID
     * @param userId       参加するユーザーのID
     * @param roomPassword 部屋のパスワード（パスワード保護された部屋の場合）
     * @throws ApplicationException 部屋が見つからない、パスワードが間違っている、既に部屋に参加している場合など
     */
    public void joinRoom(UUID roomId, UUID userId, String roomPassword) {
        roomDomainService.addRoomMember(roomId, userId, roomPassword);
    }
}
