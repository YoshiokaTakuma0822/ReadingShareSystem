package com.readingshare.auth.infrastructure.security;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

import org.springframework.stereotype.Service;

import com.readingshare.auth.domain.model.AuthToken;
import com.readingshare.auth.domain.model.User;

/**
 * 認証トークンの生成を担当するサービス。
 *
 * @author 003
 * @componentId C5
 * @moduleName 認証トークン生成サービス
 */
@Service
public class TokenGenerationService {

    private static final int TOKEN_LENGTH = 64; // 64バイト = 512ビット
    private static final long TOKEN_VALIDITY_HOURS = 24 * 7; // 7日間

    private final SecureRandom secureRandom;

    public TokenGenerationService() {
        this.secureRandom = new SecureRandom();
    }

    /**
     * 新しい認証トークンを生成する。
     *
     * @param user トークンの所有者
     * @return 生成されたトークン
     */
    public AuthToken generateToken(User user) {
        String tokenValue = generateSecureTokenValue();
        Instant now = Instant.now();
        Instant expiresAt = now.plus(TOKEN_VALIDITY_HOURS, ChronoUnit.HOURS);

        return new AuthToken(tokenValue, user, now, expiresAt);
    }

    /**
     * セキュアなランダムトークン値を生成する。
     *
     * @return Base64エンコードされたトークン値
     */
    private String generateSecureTokenValue() {
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
}
