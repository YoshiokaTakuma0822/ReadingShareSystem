package com.readingshare.auth.infrastructure.security;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import org.jspecify.annotations.NonNull;
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
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        // WebSocket handshake時はSec-WebSocket-Protocolヘッダーを検証して認証を強制
        String upgradeHeader = request.getHeader("Upgrade");
        if ("websocket".equalsIgnoreCase(upgradeHeader)) {
            String wsProtocolHeader = request.getHeader("Sec-WebSocket-Protocol");
            if (wsProtocolHeader == null || !wsProtocolHeader.startsWith(BEARER_PREFIX)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized WebSocket handshake");
                return;
            }
            // 有効なBearerトークンをサブプロトコルとして返却
            authorizationHeader = wsProtocolHeader;
            response.setHeader("Sec-WebSocket-Protocol", wsProtocolHeader);
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
