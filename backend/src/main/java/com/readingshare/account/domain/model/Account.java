package com.readingshare.account.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Domain model representing an Account entity.
 * Encapsulates account invariants and behavior.
 */
public class Account {
    private UUID id;
    private String email;
    private String password;
    private String refreshToken;
    private OffsetDateTime lastTokenRefreshAt;

    private Account(UUID id, String email, String password, String refreshToken) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.refreshToken = refreshToken;
        this.lastTokenRefreshAt = null; // Initialize as null
        validate();
    }

    /**
     * Factory method to create a new account domain object.
     * Password should be already encoded.
     */
    public static Account create(String email, String encodedPassword) {
        return new Account(null, email, encodedPassword, null);
    }

    /**
     * Reconstitute an existing account with all fields (from persistence).
     */
    public static Account from(UUID id, String email, String password, String refreshToken) {
        return new Account(id, email, password, refreshToken);
    }

    /**
     * Reconstitute an existing account with all fields including last token refresh
     * timestamp (from persistence).
     */
    public static Account from(UUID id, String email, String password, String refreshToken,
            OffsetDateTime lastTokenRefreshAt) {
        Account account = new Account(id, email, password, refreshToken);
        account.lastTokenRefreshAt = lastTokenRefreshAt;
        return account;
    }

    private void validate() {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be blank");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password must not be blank");
        }
    }

    public UUID getId() {
        return id;
    }

    /**
     * Set the ID after persisting the aggregate.
     */
    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Update the stored refresh token.
     */
    public void updateRefreshToken(String token) {
        this.refreshToken = token;
        this.lastTokenRefreshAt = OffsetDateTime.now();
    }

    /**
     * Clear the stored refresh token.
     */
    public void clearRefreshToken() {
        this.refreshToken = null;
        this.lastTokenRefreshAt = null;
    }

    public OffsetDateTime getLastTokenRefreshAt() {
        return lastTokenRefreshAt;
    }

    /**
     * Check if token was refreshed recently (within specified minutes).
     *
     * @param withinMinutes The time window in minutes to consider as "recent"
     * @return true if the token was refreshed within the specified time window
     */
    public boolean wasTokenRefreshedRecently(long withinMinutes) {
        if (lastTokenRefreshAt == null) {
            return false;
        }
        OffsetDateTime threshold = OffsetDateTime.now().minusMinutes(withinMinutes);
        return lastTokenRefreshAt.isAfter(threshold);
    }

    /**
     * Change the account password.
     * New password must be already encoded.
     */
    public void changePassword(String newEncodedPassword) {
        if (newEncodedPassword == null || newEncodedPassword.isBlank()) {
            throw new IllegalArgumentException("Password must not be blank");
        }
        this.password = newEncodedPassword;
    }
}
