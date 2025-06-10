package com.readingshare.room.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readingshare.account.infrastructure.persistence.AccountEntity;
import com.readingshare.room.infrastructure.persistence.MemberEntity;
import com.readingshare.room.infrastructure.persistence.RoomEntity;

@Repository
public interface MemberJpaRepository extends JpaRepository<MemberEntity, Long> {
    @Override
    @NonNull
    <S extends MemberEntity> S save(S entity);

    @Override
    @NonNull
    List<MemberEntity> findAll();

    @Override
    @NonNull
    Optional<MemberEntity> findById(@Nullable Long id);

    /**
     * Find members by name and account.
     *
     * @param name    the member name
     * @param account the account entity
     * @return list of members with the given name and account
     */
    @NonNull
    List<MemberEntity> findByNameAndAccount(@NonNull String name, @NonNull AccountEntity account);

    /**
     * Check if a member with the given name exists for the account.
     *
     * @param name    the member name
     * @param account the account entity
     * @return true if exists, false otherwise
     */
    boolean existsByNameAndAccount(@NonNull String name, @NonNull AccountEntity account);

    /**
     * Find all members in a specific room.
     *
     * @param room the room entity
     * @return list of members in the room
     */
    @NonNull
    List<MemberEntity> findByRoom(RoomEntity room);

    /**
     * Find a member by account and room.
     *
     * @param account the account entity
     * @param room    the room entity
     * @return the member if found, otherwise empty
     */
    @NonNull
    Optional<MemberEntity> findByAccountAndRoom(@NonNull AccountEntity account, @NonNull RoomEntity room);
}
