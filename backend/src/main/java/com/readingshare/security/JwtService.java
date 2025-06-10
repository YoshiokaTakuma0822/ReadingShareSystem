package com.readingshare.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.readingshare.account.domain.model.Account;
import com.readingshare.security.JwtService.JwtProperties;

/**
 * JWT Service using Spring Security OAuth2 Resource Server infrastructure.
 * This replaces the previous io.jsonwebtoken.Jwts implementation.
 */
@Service
@EnableConfigurationProperties(JwtProperties.class)
public class JwtService {
    // @Value("${jwt.expiration:900000}") // 15 minutes in milliseconds
    // private long jwtExpiration;

    // @Value("${jwt.refreshExpiration:604800000}") // 7 days in milliseconds
    // private long refreshExpiration;

    @ConfigurationProperties(prefix = "jwt")
    public record JwtProperties(
            @DefaultValue("900000") long expiration,
            @DefaultValue("604800000") long refreshExpiration) {
    }

    private final JwtProperties jwtProperties;

    private final JwtEncoder jwtEncoder;

    // JwtDecoder removed - validation now handled by OAuth2 Resource Server

    public JwtService(JwtProperties jwtProperties, JwtEncoder jwtEncoder) {
        this.jwtProperties = jwtProperties;
        this.jwtEncoder = jwtEncoder;
    }

    public record TokenPair(Jwt accessToken, Jwt refreshToken) {
    }

    /**
     * Generate a token pair for an account.
     */
    public TokenPair generateTokenPair(Account account) {
        var accessToken = generateToken(account);
        var refreshToken = generateRefreshToken(account);
        return new TokenPair(accessToken, refreshToken);
    }

    /**
     * Generate an access token for an account.
     */
    public Jwt generateToken(Account account) {
        return generateToken(new HashMap<>(), account);
    }

    /**
     * Generate a refresh token for an account.
     */
    public Jwt generateRefreshToken(Account account) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return buildToken(claims, account, this.jwtProperties.refreshExpiration);
    }

    /**
     * Generate token with extra claims for an account.
     */
    public Jwt generateToken(Map<String, Object> extraClaims, Account account) {
        return buildToken(extraClaims, account, this.jwtProperties.expiration);
    }

    /**
     * Build the JWT token for an account using Spring Security's JwtEncoder.
     */
    private Jwt buildToken(Map<String, Object> extraClaims, Account account,
            long expiration) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(expiration, ChronoUnit.MILLIS);

        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                .issuer("spring-bootstrap")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(account.getId().toString()) // Use UUID as subject
                .claim("roles", List.of("ROLE_USER"));
        // .claim("email", account.getEmail()); // Add email as a claim

        // Add extra claims
        extraClaims.forEach(claimsBuilder::claim);

        JwtClaimsSet claims = claimsBuilder.build();

        return jwtEncoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims));
    }

    // The following JWT validation methods have been moved to use
    // Spring Security's JwtDecoder directly in controllers for better separation of
    // concerns.
    // JWT validation is now handled by OAuth2 Resource Server infrastructure.

    /**
     * Access token expiration duration in milliseconds.
     */
    public long getJwtExpiration() {
        return this.jwtProperties.expiration;
    }

    /**
     * Refresh token expiration duration in milliseconds.
     */
    public long getRefreshExpiration() {
        return this.jwtProperties.refreshExpiration;
    }
}
