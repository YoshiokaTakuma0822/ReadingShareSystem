package com.readingshare.auth.infrastructure.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.auth.domain.model.AuthToken;
import com.readingshare.auth.domain.repository.IAuthTokenRepository;

/**
 * 認証トークンリポジトリの実装。
 */
@Repository
@Transactional
public class AuthTokenRepositoryImpl implements IAuthTokenRepository {

    private final AuthTokenJpaRepository jpaRepository;

    public AuthTokenRepositoryImpl(AuthTokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public AuthToken save(AuthToken token) {
        return jpaRepository.save(token);
    }

    @Override
    public Optional<AuthToken> findByTokenValue(String tokenValue) {
        return jpaRepository.findByTokenValue(tokenValue);
    }

    @Override
    public Optional<AuthToken> findValidTokenByValue(String tokenValue) {
        return jpaRepository.findValidTokenByValue(tokenValue, Instant.now());
    }

    @Override
    public List<AuthToken> findActiveTokensByUserId(UUID userId) {
        return jpaRepository.findActiveTokensByUserId(userId, Instant.now());
    }

    @Override
    public void deactivateAllTokensByUserId(UUID userId) {
        jpaRepository.deactivateAllTokensByUserId(userId);
    }

    @Override
    public void deleteExpiredTokens() {
        jpaRepository.deleteExpiredTokens(Instant.now());
    }
}
