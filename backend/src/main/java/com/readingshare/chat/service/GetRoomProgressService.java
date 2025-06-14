package com.readingshare.chat.service;

import com.readingshare.chat.domain.model.UserProgress;
import com.readingshare.chat.domain.repository.IProgressRepository;
import com.readingshare.common.exception.DatabaseAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 部屋の読書進捗取得のアプリケーションサービス。
 * 担当: 榎本
 */
@Service
public class GetRoomProgressService {

    private final IProgressRepository progressRepository;

    public GetRoomProgressService(IProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    /**
     * 特定の部屋の全ユーザーの進捗情報を取得する。
     * @param roomId 進捗を取得する部屋ID
     * @return 取得されたユーザー進捗リスト
     * @throws DatabaseAccessException データベースアクセスエラー時
     */
    @Transactional(readOnly = true)
    public List<UserProgress> getRoomProgress(Long roomId) {
        return progressRepository.findByRoomId(roomId);
    }
}
