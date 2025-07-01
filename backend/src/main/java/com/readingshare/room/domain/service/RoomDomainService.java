package com.readingshare.room.domain.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.common.exception.DomainException;
import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.model.RoomMember;
import com.readingshare.room.domain.repository.IRoomMemberRepository;
import com.readingshare.room.domain.repository.IRoomRepository;

/**
 * 部屋の作成とメンバー管理に関するドメインロジックを扱うサービス。
 * 担当: 芳岡
 */
@Service
public class RoomDomainService {

    private final IRoomRepository roomRepository;
    private final IRoomMemberRepository roomMemberRepository;
    private final PasswordEncoder passwordEncoder;

    public RoomDomainService(IRoomRepository roomRepository, IRoomMemberRepository roomMemberRepository,
            PasswordEncoder passwordEncoder) {
        this.roomRepository = roomRepository;
        this.roomMemberRepository = roomMemberRepository;
        this.passwordEncoder = passwordEncoder;
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
        if (rawPassword != null && !rawPassword.isEmpty()) {
            room.setHasPassword(true);
            room.setPasswordHash(passwordEncoder.encode(rawPassword));
        } else {
            room.setHasPassword(false);
            room.setPasswordHash(null);
        }
        Room savedRoom = roomRepository.save(room);
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

        // パスワード検証
        if (room.isHasPassword()) {
            if (roomPassword == null || roomPassword.isEmpty()) {
                throw new DomainException("パスワードが必要です。");
            }
            if (room.getPasswordHash() == null || !passwordEncoder.matches(roomPassword, room.getPasswordHash())) {
                throw new DomainException("パスワードが正しくありません。");
            }
        }

        // ユーザーが既にメンバーでないか確認
        Optional<RoomMember> existingOpt = roomMemberRepository.findByRoomAndUserId(room, userId);
        if (existingOpt.isPresent()) {
            // ホストの場合、明示的参加として参加日時を更新
            if (room.getHostUserId().equals(userId)) {
                RoomMember hostMember = existingOpt.get();
                hostMember.setJoinedAt(Instant.now());
                return roomMemberRepository.save(hostMember);
            }
            throw new DomainException("既に部屋のメンバーです。");
        }

        // メンバーとして追加
        RoomMember newMember = new RoomMember(room, userId, Instant.now());
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
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new DomainException("指定された部屋が見つかりません。"));
        return roomMemberRepository.findByRoomAndUserId(room, userId);
    }
}
