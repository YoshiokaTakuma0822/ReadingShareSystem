package com.readingshare.room.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.account.domain.model.Account;
import com.readingshare.account.infrastructure.persistence.AccountEntity;
import com.readingshare.account.infrastructure.repository.AccountJpaRepository;
import com.readingshare.room.domain.model.Member;
import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.repository.MemberRepository;
import com.readingshare.room.infrastructure.persistence.MemberEntity;
import com.readingshare.room.infrastructure.persistence.RoomEntity;

/**
 * Adapter implementing domain MemberRepository via JPA.
 */
@Repository
@Transactional
public class MemberRepositoryImpl implements MemberRepository {
    private final MemberJpaRepository memberJpaRepository;
    private final AccountJpaRepository accountJpaRepository;
    private final RoomJpaRepository roomJpaRepository;

    public MemberRepositoryImpl(MemberJpaRepository memberJpaRepository,
            AccountJpaRepository accountJpaRepository,
            RoomJpaRepository roomJpaRepository) {
        this.memberJpaRepository = memberJpaRepository;
        this.accountJpaRepository = accountJpaRepository;
        this.roomJpaRepository = roomJpaRepository;
    }

    @Override
    public Optional<Member> findByAccountAndRoom(Account account, Room room) {
        AccountEntity accountEntity = accountJpaRepository.findById(account.getId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + account.getId()));
        RoomEntity roomEntity = roomJpaRepository.findById(room.getId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + room.getId()));
        return memberJpaRepository.findByAccountAndRoom(accountEntity, roomEntity)
                .map(e -> Member.reconstitute(e.getId(), e.getName(), account, room));
    }

    @Override
    public boolean existsByNameAndAccount(String name, Account account) {
        AccountEntity accountEntity = accountJpaRepository.findById(account.getId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + account.getId()));
        return memberJpaRepository.existsByNameAndAccount(name, accountEntity);
    }

    @Override
    public Member save(Member member) {
        MemberEntity entity;
        if (member.getId() != null) {
            entity = memberJpaRepository.findById(member.getId()).orElse(new MemberEntity());
            entity.setId(member.getId());
        } else {
            entity = new MemberEntity();
        }
        entity.setName(member.getName());
        // map account
        AccountEntity accountEntity = accountJpaRepository.findById(member.getAccount().getId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + member.getAccount().getId()));
        entity.setAccount(accountEntity);
        // map room
        RoomEntity roomEntity = roomJpaRepository.findById(member.getRoom().getId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + member.getRoom().getId()));
        entity.setRoom(roomEntity);
        MemberEntity saved = memberJpaRepository.save(entity);
        member.setId(saved.getId());
        return member;
    }

    @Override
    public List<Member> findByRoom(Room room) {
        RoomEntity roomEntity = roomJpaRepository.findById(room.getId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + room.getId()));
        return memberJpaRepository.findByRoom(roomEntity).stream()
                .map(e -> Member.reconstitute(
                        e.getId(),
                        e.getName(),
                        // map account entity to domain
                        Account.from(
                                e.getAccount().getId(),
                                e.getAccount().getEmail(),
                                e.getAccount().getPassword(),
                                e.getAccount().getRefreshToken()),
                        room))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Member member) {
        memberJpaRepository.deleteById(member.getId());
    }

    @Override
    public Optional<Member> findById(Long id) {
        return memberJpaRepository.findById(id)
                .map(e -> Member.reconstitute(
                        e.getId(),
                        e.getName(),
                        Account.from(
                                e.getAccount().getId(),
                                e.getAccount().getEmail(),
                                e.getAccount().getPassword(),
                                e.getAccount().getRefreshToken()),
                        // map room entity to domain
                        Room.reconstitute(
                                e.getRoom().getId(),
                                e.getRoom().getName(),
                                e.getRoom().getDescription(),
                                e.getRoom().getCreatedBy().getId())));
    }
}
