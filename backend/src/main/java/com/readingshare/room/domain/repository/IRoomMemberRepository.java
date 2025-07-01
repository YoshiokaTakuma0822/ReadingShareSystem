package com.readingshare.room.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.readingshare.room.domain.model.Room;
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
     * @param room 部屋エンティティ
     * @return 部屋のメンバーリスト
     */
    List<RoomMember> findByRoom(Room room);

    /**
     * 特定の部屋とユーザーの組み合わせでメンバーを検索する。
     *
     * @param room   部屋エンティティ
     * @param userId ユーザーID
     * @return メンバーが見つかった場合はOptionalにRoomMember、見つからない場合はOptional.empty()
     */
    Optional<RoomMember> findByRoomAndUserId(Room room, UUID userId);

    /**
     * 部屋メンバー情報を削除する。
     *
     * @param roomMember 削除する部屋メンバーエンティティ
     */
    void delete(RoomMember roomMember);

    /**
     * 指定ユーザーの部屋参加履歴（joinedAt降順）を取得する。
     *
     * @param userId   ユーザーID
     * @param pageable ページ情報
     * @return 参加履歴リスト
     */
    List<RoomMember> findByUserIdOrderByJoinedAtDesc(UUID userId, Pageable pageable);
}
