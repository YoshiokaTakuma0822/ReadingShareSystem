package com.readingshare.account.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readingshare.account.infrastructure.persistence.AccountEntity;

/**
 * Repository for account operations.
 */
@Repository
public interface AccountJpaRepository extends JpaRepository<AccountEntity, UUID> {
    @Override
    @NonNull
    <S extends AccountEntity> S save(S entity);

    @Override
    @NonNull
    List<AccountEntity> findAll();

    /**
     * Find an account by email.
     *
     * @param email the email to search for
     * @return the account if found
     */
    Optional<AccountEntity> findByEmail(String email);

    /**
     * Find an account by stored refresh token.
     */
    Optional<AccountEntity> findByRefreshToken(String refreshToken);
}
