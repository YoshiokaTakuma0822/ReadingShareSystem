package com.readingshare.account.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.readingshare.account.domain.model.Account;

/**
 * Domain repository interface for Account aggregate.
 * Implemented in the infrastructure layer using JPA.
 */
public interface AccountRepository {
    Optional<Account> findById(UUID id);

    Optional<Account> findByEmail(String email);

    Account save(Account account);
}
