package com.readingshare.room.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.readingshare.room.domain.model.RoomMember;
import com.readingshare.room.domain.repository.IRoomMemberRepository;

/**
 * 部屋メンバーリポジトリの実装クラス。
 * データベース操作を担当します。
 */
@Repository
public class RoomMemberRepositoryImpl implements IRoomMemberRepository {

    @Autowired
    private RoomMemberJpaRepository roomMemberRepository;

    /**
     * 部屋メンバーを保存します。
     *
     * @param roomMember 保存する部屋メンバー
     * @return 保存された部屋メンバー
     */
    @Override
    public RoomMember save(RoomMember roomMember) {
        return roomMemberRepository.save(roomMember);
    }

    /**
     * 指定された部屋IDに関連付けられた部屋メンバーを取得します。
     *
     * @param roomId 部屋ID
     * @return 指定された部屋IDに関連付けられた部屋メンバーのリスト
     */
    @Override
    public List<RoomMember> findByRoomId(UUID roomId) {
        return roomMemberRepository.findByRoomId(roomId);
    }

    /**
     * 指定された部屋IDとユーザーIDに関連付けられた部屋メンバーを取得します。
     *
     * @param roomId 部屋ID
     * @param userId ユーザーID
     * @return 指定された部屋IDとユーザーIDに関連付けられた部屋メンバーを含むOptional、見つからない場合は空のOptional
     */
    @Override
    public Optional<RoomMember> findByRoomIdAndUserId(UUID roomId, UUID userId) {
        return roomMemberRepository.findByRoomIdAndUserId(roomId, userId);
    }
}
