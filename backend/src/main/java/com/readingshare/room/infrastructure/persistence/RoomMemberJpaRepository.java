package com.readingshare.room.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readingshare.room.domain.model.RoomMember;

/**
 * 部屋メンバー情報のJPAリポジトリインターフェース。
 * 
 * @author 23004
 */
@Repository
public interface RoomMemberJpaRepository extends JpaRepository<RoomMember, UUID> {

    /**
     * 特定の部屋の全メンバーを取得する。
     *
     * @param roomId 部屋ID
     * @return 部屋のメンバーリスト
     */
    List<RoomMember> findByRoomId(UUID roomId);

    /**
     * 特定の部屋とユーザーの組み合わせでメンバーを検索する。
     *
     * @param roomId 部屋ID
     * @param userId ユーザーID
     * @return メンバーが見つかった場合はOptionalにRoomMember、見つからない場合はOptional.empty()
     */
    Optional<RoomMember> findByRoomIdAndUserId(UUID roomId, UUID userId);
}
