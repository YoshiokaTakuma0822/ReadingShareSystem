package com.readingshare.account.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.account.domain.model.Account;
import com.readingshare.account.domain.service.AccountDomainService;

/**
 * Application service delegating account operations to domain layer.
 */
@Service
public class AccountService {
    private final AccountDomainService accountDomainService;

    public AccountService(AccountDomainService accountDomainService) {
        this.accountDomainService = accountDomainService;
    }

    /**
     * Find an account by ID.
     */
    @Transactional(readOnly = true)
    public Optional<Account> getAccount(UUID id) {
        return accountDomainService.getAccount(id);
    }

    /**
     * Create a new account with raw password.
     */
    @Transactional
    public Account createAccount(String email, String rawPassword) {
        return accountDomainService.createAccount(email, rawPassword);
    }

    /**
     * Find an account by email.
     */
    @Transactional(readOnly = true)
    public Optional<Account> findByEmail(String email) {
        return accountDomainService.findByEmail(email);
    }

    /**
     * Update the refresh token for the account.
     */
    @Transactional
    public void updateRefreshToken(UUID accountId, String refreshToken) {
        accountDomainService.updateRefreshToken(accountId, refreshToken);
    }

    /**
     * Clear the refresh token for the account.
     */
    @Transactional
    public void clearRefreshToken(UUID accountId) {
        accountDomainService.clearRefreshToken(accountId);
    }

    // Removed direct JPA access; use domain repository

    // Other mapping methods can be added if entity access is still needed
}
