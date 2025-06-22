package com.readingshare.chat.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.chat.domain.model.ChatMessage;
import com.readingshare.chat.domain.repository.IChatMessageRepository;
import com.readingshare.common.exception.ApplicationException;
import com.readingshare.common.exception.DatabaseAccessException;
import com.readingshare.room.domain.repository.IRoomRepository;

/**
 * チャット履歴取得のアプリケーションサービス。
 *
 * @author 02001
 * @componentId C4
 * @moduleName チャット履歴取得サービス
 * @see IChatMessageRepository
 */
@Service
public class GetChatHistoryService {

    private final IChatMessageRepository chatMessageRepository;
    private final IRoomRepository roomRepository;

    public GetChatHistoryService(IChatMessageRepository chatMessageRepository, IRoomRepository roomRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.roomRepository = roomRepository;
    }

    /**
     * 特定の部屋のチャット履歴を取得する。
     *
     * @param roomId 履歴を取得する部屋のID
     * @return チャットメッセージのリスト
     * @throws ApplicationException    部屋が存在しない場合
     * @throws DatabaseAccessException データベースアクセスエラー時
     */
    @Transactional(readOnly = true)
    public List<ChatMessage> getChatHistory(UUID roomId) {
        // 部屋の存在チェック
        if (!roomRepository.findById(roomId).isPresent()) {
            throw new ApplicationException("Room not found. Room ID: " + roomId);
        }

        return chatMessageRepository.findByRoomId(roomId);
    }
}
