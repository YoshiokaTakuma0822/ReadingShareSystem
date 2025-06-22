package com.readingshare.room.domain.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.auth.infrastructure.security.IPasswordHasher; // パスワードハッシュ化のために利用
import com.readingshare.common.exception.DomainException;
import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.model.RoomMember;
import com.readingshare.room.domain.repository.IRoomMemberRepository;
import com.readingshare.room.domain.repository.IRoomRepository;

/**
 * 部屋の作成とメンバー管理に関するドメインロジックを扱うサービス。
 *
 * @author 02004
 * @componentId C3
 * @moduleName ルームドメインサービス
 * @see IRoomRepository
 * @see IRoomMemberRepository
 * @see IPasswordHasher
 */
@Service
public class RoomDomainService {

    private final IRoomRepository roomRepository;
    private final IRoomMemberRepository roomMemberRepository;
    private final IPasswordHasher passwordHasher; // パスワードハッシュ化のために注入

    public RoomDomainService(IRoomRepository roomRepository, IRoomMemberRepository roomMemberRepository,
            IPasswordHasher passwordHasher) {
        this.roomRepository = roomRepository;
        this.roomMemberRepository = roomMemberRepository;
        this.passwordHasher = passwordHasher;
    }

    /**
     * 新しい部屋を作成し、ホストをそのメンバーとして追加する。
     *
     * @param room        作成する部屋エンティティ（パスワードハッシュは含まない）
     * @param rawPassword 平文の部屋パスワード（オプション）
     * @return 作成された部屋エンティティ
     * @throws DomainException 部屋の作成に失敗した場合（例: 同じ部屋名が存在する場合など）
     */
    @Transactional
    public Room createRoom(Room room, String rawPassword) {
        // パスワードが設定されていればハッシュ化
        if (rawPassword != null && !rawPassword.isEmpty()) {
            room.setRoomPasswordHash(passwordHasher.hashPassword(rawPassword));
        }

        // 部屋を保存
        Room savedRoom = roomRepository.save(room);

        // ホストを部屋のメンバーとして追加
        RoomMember hostMember = new RoomMember(savedRoom.getId(), room.getHostUserId(), Instant.now());
        roomMemberRepository.save(hostMember);

        return savedRoom;
    }

    /**
     * 部屋にメンバーを追加する。
     * パスワードが設定されている場合は検証を行う。
     *
     * @param roomId       部屋ID
     * @param userId       ユーザーID
     * @param roomPassword 部屋のパスワード（オプション）
     * @return 作成されたRoomMemberエンティティ
     * @throws DomainException メンバー追加に失敗した場合
     */
    @Transactional
    public RoomMember addRoomMember(UUID roomId, UUID userId, String roomPassword) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new DomainException("指定された部屋が見つかりません。"));

        // パスワードが設定されている場合は検証
        if (room.getRoomPasswordHash() != null) {
            if (roomPassword == null || !passwordHasher.verifyPassword(roomPassword, room.getRoomPasswordHash())) {
                throw new DomainException("部屋のパスワードが正しくありません。");
            }
        }

        // ユーザーが既にメンバーでないか確認
        if (roomMemberRepository.findByRoomIdAndUserId(roomId, userId).isPresent()) {
            throw new DomainException("既に部屋のメンバーです。");
        }

        // メンバーとして追加
        RoomMember newMember = new RoomMember(roomId, userId, Instant.now());
        return roomMemberRepository.save(newMember);
    }

    /**
     * 指定された部屋とユーザーの組み合わせでメンバー情報を取得する。
     *
     * @param roomId 部屋ID
     * @param userId ユーザーID
     * @return メンバー情報が見つかった場合はOptionalにRoomMember、見つからない場合はOptional.empty()
     */
    @Transactional(readOnly = true)
    public Optional<RoomMember> getRoomMember(UUID roomId, UUID userId) {
        return roomMemberRepository.findByRoomIdAndUserId(roomId, userId);
    }
}
