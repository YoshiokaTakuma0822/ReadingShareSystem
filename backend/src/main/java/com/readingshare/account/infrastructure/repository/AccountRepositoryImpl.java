package com.readingshare.account.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.account.domain.model.Account;
import com.readingshare.account.domain.repository.AccountRepository;
import com.readingshare.account.infrastructure.persistence.AccountEntity;

/**
 * Adapter implementing domain repository by delegating to Spring Data
 * interface.
 */
@Repository
@Transactional
public class AccountRepositoryImpl implements AccountRepository {
    private final AccountJpaRepository accountJpaRepository;

    public AccountRepositoryImpl(AccountJpaRepository springRepo) {
        this.accountJpaRepository = springRepo;
    }

    @Override
    public Optional<Account> findById(UUID id) {
        return accountJpaRepository.findById(id)
                .map(entity -> Account.from(
                        entity.getId(),
                        entity.getEmail(),
                        entity.getPassword(),
                        entity.getRefreshToken(),
                        entity.getLastTokenRefreshAt()));
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        return accountJpaRepository.findByEmail(email)
                .map(entity -> Account.from(
                        entity.getId(),
                        entity.getEmail(),
                        entity.getPassword(),
                        entity.getRefreshToken(),
                        entity.getLastTokenRefreshAt()));
    }

    @Override
    public Account save(Account account) {
        AccountEntity entity;
        if (account.getId() != null) {
            entity = accountJpaRepository.findById(account.getId())
                    .orElse(new AccountEntity());
            entity.setId(account.getId());
        } else {
            entity = new AccountEntity();
        }
        entity.setEmail(account.getEmail());
        entity.setPassword(account.getPassword());
        entity.setRefreshToken(account.getRefreshToken());
        entity.setLastTokenRefreshAt(account.getLastTokenRefreshAt());
        AccountEntity saved = accountJpaRepository.save(entity);
        account.setId(saved.getId());
        return account;
    }
}
