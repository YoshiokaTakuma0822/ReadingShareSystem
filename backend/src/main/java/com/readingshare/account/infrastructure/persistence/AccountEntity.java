package com.readingshare.account.infrastructure.persistence;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity representing user accounts used for authentication.
 * This is separated from MemberEntity which is used for chat functionality.
 */
@Entity
@Table(name = "accounts")
public class AccountEntity {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    private String email;
    private String password;

    /**
     * Opaque refresh token stored for this account.
     */
    @Column(name = "refresh_token")
    private String refreshToken;

    /**
     * Timestamp of the last JWT token refresh.
     * Used to prevent too frequent token refreshes.
     */
    @Column(name = "last_token_refresh_at")
    private OffsetDateTime lastTokenRefreshAt;

    // Constructors
    public AccountEntity() {
    }

    public AccountEntity(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public OffsetDateTime getLastTokenRefreshAt() {
        return lastTokenRefreshAt;
    }

    public void setLastTokenRefreshAt(OffsetDateTime lastTokenRefreshAt) {
        this.lastTokenRefreshAt = lastTokenRefreshAt;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AccountEntity account = (AccountEntity) o;
        return id != null && id.equals(account.id);
    }
}
