package com.readingshare.account.domain.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.account.domain.model.Account;
import com.readingshare.account.domain.repository.AccountRepository;

/**
 * Domain service for Account-related operations.
 */
@Service
@Transactional
public class AccountDomainService {
    private final AccountRepository accountRepo;
    private final PasswordEncoder passwordEncoder;

    public AccountDomainService(AccountRepository accountRepo, PasswordEncoder passwordEncoder) {
        this.accountRepo = accountRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Create a new account, encoding the raw password.
     */
    public Account createAccount(String email, String rawPassword) {
        String encoded = passwordEncoder.encode(rawPassword);
        Account account = Account.create(email, encoded);
        return accountRepo.save(account);
    }

    /**
     * Find account by ID.
     */
    @Transactional(readOnly = true)
    public Optional<Account> getAccount(UUID id) {
        return accountRepo.findById(id);
    }

    /**
     * Find account by email.
     */
    @Transactional(readOnly = true)
    public Optional<Account> findByEmail(String email) {
        return accountRepo.findByEmail(email);
    }

    /**
     * Update the refresh token for the account.
     */
    public void updateRefreshToken(UUID accountId, String refreshToken) {
        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));
        account.updateRefreshToken(refreshToken);
        accountRepo.save(account);
    }

    /**
     * Clear the refresh token.
     */
    public void clearRefreshToken(UUID accountId) {
        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));
        account.clearRefreshToken();
        accountRepo.save(account);
    }
}
