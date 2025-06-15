package com.readingshare.room.domain.repository;

import java.util.List;
import java.util.Optional;

import com.readingshare.room.domain.model.RoomMember;

/**
 * 部屋メンバー情報の永続化を担当するリポジトリインターフェース。
 * 担当: 芳岡
 */
public interface IRoomMemberRepository {

    /**
     * 部屋メンバー情報を保存する。
     *
     * @param roomMember 保存する部屋メンバーエンティティ
     * @return 保存された部屋メンバーエンティティ
     */
    RoomMember save(RoomMember roomMember);

    /**
     * 特定の部屋の全メンバーを取得する。
     *
     * @param roomId 部屋ID
     * @return 部屋のメンバーリスト
     */
    List<RoomMember> findByRoomId(Long roomId);

    /**
     * 特定の部屋とユーザーの組み合わせでメンバーを検索する。
     *
     * @param roomId 部屋ID
     * @param userId ユーザーID
     * @return メンバーが見つかった場合はOptionalにRoomMember、見つからない場合はOptional.empty()
     */
    Optional<RoomMember> findByRoomIdAndUserId(Long roomId, Long userId);
}
