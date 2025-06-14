package com.readingshare.room.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readingshare.room.domain.model.RoomMember;

/**
 * 部屋メンバー情報のJPAリポジトリインターフェース。
 * 担当: 芳岡
 */
@Repository
public interface RoomMemberRepository extends JpaRepository<RoomMember, Long> {

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
