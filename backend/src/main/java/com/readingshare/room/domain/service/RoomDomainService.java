package com.readingshare.room.domain.service;

import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.model.RoomId;
import com.readingshare.room.domain.model.RoomMember;
import com.readingshare.room.domain.repository.IRoomRepository;
import com.readingshare.room.domain.repository.IRoomMemberRepository;
import com.readingshare.auth.infrastructure.security.IPasswordHasher; // パスワードハッシュ化のために利用
import com.readingshare.common.exception.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/**
 * 部屋の作成とメンバー管理に関するドメインロジックを扱うサービス。
 * 担当: 芳岡
 */
@Service
public class RoomDomainService {

    private final IRoomRepository roomRepository;
    private final IRoomMemberRepository roomMemberRepository;
    private final IPasswordHasher passwordHasher; // パスワードハッシュ化のために注入

    public RoomDomainService(IRoomRepository roomRepository, IRoomMemberRepository roomMemberRepository, IPasswordHasher passwordHasher) {
        this.roomRepository = roomRepository;
        this.roomMemberRepository = roomMemberRepository;
        this.passwordHasher = passwordHasher;
    }

    /**
     * 新しい部屋を作成し、ホストをそのメンバーとして追加する。
     * @param room 作成する部屋エンティティ（ID、パスワードハッシュは含まない）
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
        RoomMember hostMember = new RoomMember(savedRoom.getId(), savedRoom.getHostUserId(), Instant.now());
        roomMemberRepository.save(hostMember);

        return savedRoom;
    }

    /**
     * ユーザーを部屋のメンバーとして追加する。
     * @param roomId 参加する部屋のID
     * @param userId 参加するユーザーのID
     * @param roomPassword 部屋のパスワード（パスワード保護された部屋の場合に必要）
     * @throws DomainException 部屋が見つからない、パスワードが間違っている、既に部屋に参加している場合など
     */
    @Transactional
    public void addRoomMember(RoomId roomId, Long userId, String roomPassword) {
        Optional<Room> roomOptional = roomRepository.findById(roomId);
        if (roomOptional.isEmpty()) {
            throw new DomainException("Room not found with ID: " + roomId.getValue());
        }
        Room room = roomOptional.get();

        // パスワード保護された部屋の場合、パスワード検証
        if (room.hasPassword()) {
            if (roomPassword == null || !passwordHasher.verifyPassword(roomPassword, room.getRoomPasswordHash())) {
                throw new DomainException("Incorrect room password.");
            }
        }

        // 既にメンバーであるかチェック
        if (roomMemberRepository.findByRoomIdAndUserId(roomId.getValue(), userId).isPresent()) {
            throw new DomainException("User is already a member of room: " + roomId.getValue());
        }

        // メンバーとして追加
        RoomMember newMember = new RoomMember(roomId.getValue(), userId, Instant.now());
        roomMemberRepository.save(newMember);
        // room.addMember(newMember); // Roomエンティティのコレクションに追加する場合（RoomMemberは単独で管理も可能）
        // roomRepository.save(room); // Roomエンティティの変更を保存する場合
    }

    /**
     * 特定の部屋のメンバーを検索する。
     * @param roomId 部屋ID
     * @return 部屋のメンバーリスト
     */
    @Transactional(readOnly = true)
    public Optional<RoomMember> getRoomMember(Long roomId, Long userId) {
        return roomMemberRepository.findByRoomIdAndUserId(roomId, userId);
    }
}