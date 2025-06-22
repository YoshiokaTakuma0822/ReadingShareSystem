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
 *
 * @author 003
 * @componentId C5
 * @moduleName Token認証フィルター
 */
@Component
public class BearerTokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final IAuthTokenRepository authTokenRepository;

    /**
     * コンストラクタ。
     *
     * @param authTokenRepository 認証トークンリポジトリ
     */
    public BearerTokenAuthenticationFilter(IAuthTokenRepository authTokenRepository) {
        this.authTokenRepository = authTokenRepository;
    }

    /**
     * リクエストをフィルタリングし、Bearer Token認証を処理します。
     *
     * @param request     HTTPリクエスト
     * @param response    HTTPレスポンス
     * @param filterChain フィルタチェーン
     * @throws ServletException サーブレット例外が発生した場合
     * @throws IOException      入出力例外が発生した場合
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

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
