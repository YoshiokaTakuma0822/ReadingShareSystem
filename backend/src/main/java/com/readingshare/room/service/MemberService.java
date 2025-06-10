package com.readingshare.room.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.account.domain.model.Account;
import com.readingshare.account.domain.repository.AccountRepository;
import com.readingshare.room.domain.model.Member;
import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.repository.MemberRepository;
import com.readingshare.room.domain.repository.RoomRepository;
import com.readingshare.room.dto.MemberResponse;

/**
 * Service for member-related operations.
 * Uses domain repositories following DDD principles.
 */
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final RoomRepository roomRepository;

    public MemberService(
            MemberRepository memberRepository,
            AccountRepository accountRepository,
            RoomRepository roomRepository) {
        this.memberRepository = memberRepository;
        this.accountRepository = accountRepository;
        this.roomRepository = roomRepository;
    }

    /**
     * Find a member by ID.
     *
     * @param id the member ID
     * @return the member DTO if found, otherwise empty
     */
    @Transactional(readOnly = true)
    public @NonNull Optional<MemberResponse> getMemberById(@Nullable Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return memberRepository.findById(id)
                .map(this::toMemberResponseFromDomain);
    }

    /**
     * Convert a domain Member to DTO.
     */
    private MemberResponse toMemberResponseFromDomain(Member member) {
        Long roomId = member.getRoom() != null ? member.getRoom().getId() : null;
        return new MemberResponse(member.getId(), member.getName(), roomId);
    }

    /**
     * Create a new member in a room.
     *
     * @param name      the member name
     * @param accountId the ID of the account to associate with
     * @param roomId    the ID of the room to associate with
     * @return the created member as DTO
     * @throws IllegalArgumentException if account or room is not found with the
     *                                  given ID, or if a member with the same name
     *                                  already exists for the account
     */
    @Transactional
    public @NonNull MemberResponse createMemberInRoom(@NonNull String name, @NonNull UUID accountId,
            @NonNull Long roomId) {
        // Get the account
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));

        // Get the room
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + roomId));

        // Check if a member with this name already exists for this account in this room
        if (memberRepository.findByAccountAndRoom(account, room).isPresent()) {
            throw new IllegalArgumentException("Member already exists for this account in this room");
        }

        // Create and save the member
        Member member = Member.create(name, account, room);
        Member savedMember = memberRepository.save(member);

        return toMemberResponseFromDomain(savedMember);
    }

    /**
     * Get all members in a specific room by room ID.
     *
     * @param roomId the room ID
     * @return list of member DTOs in the room
     * @throws IllegalArgumentException if room is not found
     */
    @Transactional(readOnly = true)
    public @NonNull List<MemberResponse> getAllMembersByRoomId(@NonNull Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + roomId));
        return memberRepository.findByRoom(room).stream()
                .map(this::toMemberResponseFromDomain)
                .toList();
    }

    /**
     * Get member by account and room.
     *
     * @param accountId the account ID
     * @param roomId    the room ID
     * @return the member DTO if found
     */
    @Transactional(readOnly = true)
    public @NonNull Optional<MemberResponse> getMemberByAccountAndRoom(@NonNull UUID accountId, @NonNull Long roomId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + roomId));

        return memberRepository.findByAccountAndRoom(account, room)
                .map(this::toMemberResponseFromDomain);
    }

    /**
     * Remove a member by ID.
     *
     * @param memberId the member ID to remove
     */
    @Transactional
    public void removeMember(@NonNull Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));
        memberRepository.delete(member);
    }
}
