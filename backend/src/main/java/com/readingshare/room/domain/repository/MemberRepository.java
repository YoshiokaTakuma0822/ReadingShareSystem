package com.readingshare.room.domain.repository;

import java.util.List;
import java.util.Optional;

import com.readingshare.account.domain.model.Account;
import com.readingshare.room.domain.model.Member;
import com.readingshare.room.domain.model.Room;

/**
 * Domain repository interface for Member aggregate of a Room.
 */
public interface MemberRepository {
    Optional<Member> findByAccountAndRoom(Account account, Room room);

    boolean existsByNameAndAccount(String name, Account account);

    Member save(Member member);

    List<Member> findByRoom(Room room);

    void delete(Member member);

    /** Find a Member by its ID. */
    Optional<Member> findById(Long id);
}
