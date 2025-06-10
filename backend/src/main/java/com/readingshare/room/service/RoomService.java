package com.readingshare.room.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.account.domain.model.Account;
import com.readingshare.account.domain.repository.AccountRepository;
import com.readingshare.room.domain.model.Member;
import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.repository.MemberRepository;
import com.readingshare.room.domain.repository.RoomRepository;
import com.readingshare.room.dto.MemberResponse;
import com.readingshare.room.dto.RoomResponse;

/**
 * Application service for chat rooms using domain repositories.
 */
@Service
public class RoomService {
    private final RoomRepository roomRepo;
    private final MemberRepository memberRepo;
    private final AccountRepository accountRepo;

    public RoomService(RoomRepository roomRepo,
            MemberRepository memberRepo,
            AccountRepository accountRepo) {
        this.roomRepo = roomRepo;
        this.memberRepo = memberRepo;
        this.accountRepo = accountRepo;
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getAllRooms() {
        return roomRepo.findAll().stream()
                .map(this::toRoomResponseFromDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<RoomResponse> getRoomById(Long roomId) {
        return roomRepo.findById(roomId)
                .map(this::toRoomResponseFromDomain);
    }

    private RoomResponse toRoomResponseFromDomain(Room room) {
        return new RoomResponse(room.getId(), room.getName(), room.getDescription());
    }

    @Transactional
    public RoomResponse createRoom(String name, String description, UUID creatorId) {
        if (roomRepo.existsByName(name)) {
            throw new IllegalArgumentException("Room already exists: " + name);
        }
        Room room = Room.create(name, description, creatorId);
        Room saved = roomRepo.save(room);
        return toRoomResponseFromDomain(saved);
    }

    @Transactional
    public MemberResponse joinRoom(Long roomId, UUID accountId, String displayName) {
        Room room = roomRepo.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));
        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));

        // Check if member already exists
        Optional<Member> existingMember = memberRepo.findByAccountAndRoom(account, room);
        if (existingMember.isPresent()) {
            throw new IllegalArgumentException("Member already exists in this room");
        }

        Member member = Member.create(displayName, account, room);
        Member savedMember = memberRepo.save(member);
        return toMemberResponseFromDomain(savedMember);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getMembers(Long roomId) {
        Room room = roomRepo.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));
        return memberRepo.findByRoom(room).stream()
                .map(this::toMemberResponseFromDomain)
                .toList();
    }

    @Transactional
    public void removeMember(Long roomId, Long memberId) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + memberId));

        // Verify the member belongs to the specified room
        if (!member.getRoom().getId().equals(roomId)) {
            throw new IllegalArgumentException("Member does not belong to the specified room");
        }

        memberRepo.delete(member);
    }

    @Transactional(readOnly = true)
    public Optional<MemberResponse> getMemberByAccountAndRoom(UUID accountId, Long roomId) {
        Room room = roomRepo.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));
        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));
        return memberRepo.findByAccountAndRoom(account, room)
                .map(this::toMemberResponseFromDomain);
    }

    @Transactional
    public MemberResponse createMemberInRoom(String name, UUID accountId, Long roomId) {
        Room room = roomRepo.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));
        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));

        // Check if member already exists
        if (memberRepo.findByAccountAndRoom(account, room).isPresent()) {
            throw new IllegalArgumentException("Member already exists for this account in this room");
        }

        Member member = Member.create(name, account, room);
        Member savedMember = memberRepo.save(member);
        return toMemberResponseFromDomain(savedMember);
    }

    /**
     * Convert a domain Member to DTO.
     */
    private MemberResponse toMemberResponseFromDomain(Member member) {
        Long roomId = member.getRoom() != null ? member.getRoom().getId() : null;
        return new MemberResponse(member.getId(), member.getName(), roomId);
    }
}
