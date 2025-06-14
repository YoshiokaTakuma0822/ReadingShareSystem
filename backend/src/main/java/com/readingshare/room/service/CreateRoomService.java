package com.readingshare.room.service;

import org.springframework.stereotype.Service;

import com.readingshare.common.exception.ApplicationException;
import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.service.RoomDomainService;

/**
 * 部屋作成のアプリケーションサービス。
 * 担当: 芳岡
 */
@Service
public class CreateRoomService {

    private final RoomDomainService roomDomainService;

    public CreateRoomService(RoomDomainService roomDomainService) {
        this.roomDomainService = roomDomainService;
    }

    /**
     * 新しい部屋を作成する。
     *
     * @param roomName   部屋名
     * @param bookTitle  読んでいる本のタイトル
     * @param hostUserId 部屋を作成するユーザーのID（ホスト）
     * @return 作成された部屋のエンティティ
     * @throws ApplicationException 部屋作成に失敗した場合
     */
    public Room createRoom(String roomName, String bookTitle, Long hostUserId) {
        Room newRoom = new Room(roomName, bookTitle, hostUserId);
        // パスワードはオプションなので、ここではnullを渡す
        return roomDomainService.createRoom(newRoom, null);
    }

    /**
     * パスワード付きの新しい部屋を作成する。
     *
     * @param roomName     部屋名
     * @param bookTitle    読んでいる本のタイトル
     * @param hostUserId   部屋を作成するユーザーのID（ホスト）
     * @param roomPassword 部屋のパスワード
     * @return 作成された部屋のエンティティ
     * @throws ApplicationException 部屋作成に失敗した場合
     */
    public Room createRoomWithPassword(String roomName, String bookTitle, Long hostUserId, String roomPassword) {
        Room newRoom = new Room(roomName, bookTitle, hostUserId);
        return roomDomainService.createRoom(newRoom, roomPassword);
    }
}
