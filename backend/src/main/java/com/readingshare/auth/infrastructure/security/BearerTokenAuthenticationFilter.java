package com.readingshare.auth.infrastructure.security;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.readingshare.auth.domain.model.AuthToken;
import com.readingshare.auth.domain.repository.IAuthTokenRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Bearer Token認証を処理するフィルター。
 */
@Component
public class BearerTokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final IAuthTokenRepository authTokenRepository;

    public BearerTokenAuthenticationFilter(IAuthTokenRepository authTokenRepository) {
        this.authTokenRepository = authTokenRepository;
    }

    @Override
    protected void doFilterInternal(
            @org.springframework.lang.NonNull HttpServletRequest request,
            @org.springframework.lang.NonNull HttpServletResponse response,
            @org.springframework.lang.NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

        // WebSocket handshakeリクエストの場合は認証をスキップ
        String upgradeHeader = request.getHeader("Upgrade");
        if (upgradeHeader != null && "websocket".equalsIgnoreCase(upgradeHeader)) {
            System.out.println("[BearerTokenAuthenticationFilter] WebSocket handshakeリクエストのため認証スキップ: " + request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            String token = authorizationHeader.substring(BEARER_PREFIX.length());

            Optional<AuthToken> authToken = authTokenRepository.findValidTokenByValue(token);

            if (authToken.isPresent()) {
                AuthToken validToken = authToken.get();

                // 最終使用時刻を更新
                validToken.setLastUsedAt(Instant.now());
                authTokenRepository.save(validToken);

                // Spring SecurityのコンテキストにUserDetailsを設定
                UserPrincipal userPrincipal = new UserPrincipal(validToken.getUser());
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userPrincipal, null, userPrincipal.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
