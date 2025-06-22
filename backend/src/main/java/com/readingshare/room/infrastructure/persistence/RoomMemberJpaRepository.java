package com.readingshare.room.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readingshare.room.domain.model.RoomMember;
import com.readingshare.room.domain.model.Room;

/**
 * 部屋メンバー情報のJPAリポジトリインターフェース。
 * 担当: 芳岡
 */
@Repository
public interface RoomMemberJpaRepository extends JpaRepository<RoomMember, UUID> {

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
     * @param room 部屋エンティティ
     * @param userId ユーザーID
     * @return メンバーが見つかった場合はOptionalにRoomMember、見つからない場合はOptional.empty()
     */
    Optional<RoomMember> findByRoomAndUserId(Room room, UUID userId);
}
