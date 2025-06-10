package com.readingshare.security;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Represents the structure of JWT payload claims.
 * This record is specifically for JWT token data and should not be used
 * directly as UserDetails for Spring Security authentication.
 */
public record JwtPayload(
        String sub, // subject (user UUID)
        List<String> roles, // user roles/authorities
        Instant iat, // issued at (timestamp)
        Instant exp // expiration (timestamp)
) {
    /**
     * Create JwtPayload from Spring Security's Jwt
     */
    public static JwtPayload fromJwt(Jwt jwt) {
        return new JwtPayload(
                jwt.getSubject(),
                jwt.getClaimAsStringList("roles"),
                jwt.getIssuedAt(),
                jwt.getExpiresAt());
    }

    /**
     * Convert to Claims Map for JWT building
     */
    public Map<String, Object> toClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        return claims;
    }

    /**
     * Get user authorities as GrantedAuthority collection for Spring Security
     */
    // public Collection<? extends GrantedAuthority> getAuthorities() {
    // return roles.stream()
    // .map(role -> (GrantedAuthority) () -> role)
    // .toList();
    // }

    /**
     * Get user identifier for convenience
     */
    public String getUsername() {
        return sub; // subject is the user UUID
    }
}
