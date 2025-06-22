package com.readingshare.auth.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.auth.domain.model.AuthToken;
import com.readingshare.auth.domain.model.User;
import com.readingshare.auth.domain.repository.IAuthTokenRepository;
import com.readingshare.auth.domain.repository.IUserRepository;
import com.readingshare.auth.domain.service.AuthenticationDomainService;
import com.readingshare.auth.dto.LoginResponse;
import com.readingshare.auth.infrastructure.security.TokenGenerationService;
import com.readingshare.common.exception.ApplicationException;

/**
 * ユーザーログインのアプリケーションサービス。
 *
 * @author 003
 * @componentIdName C02 ログイン・会員登録
 * @moduleIdName M0220 ログインサービス
 * @dependsOn M0210 認証ドメインサービス
 */
@Service
@Transactional
public class LoginService {

    private final AuthenticationDomainService authenticationDomainService;
    private final IUserRepository userRepository;
    private final IAuthTokenRepository authTokenRepository;
    private final TokenGenerationService tokenGenerationService;

    public LoginService(
            AuthenticationDomainService authenticationDomainService,
            IUserRepository userRepository,
            IAuthTokenRepository authTokenRepository,
            TokenGenerationService tokenGenerationService) {
        this.authenticationDomainService = authenticationDomainService;
        this.userRepository = userRepository;
        this.authTokenRepository = authTokenRepository;
        this.tokenGenerationService = tokenGenerationService;
    }

    /**
     * ユーザー認証を行い、Bearer Tokenを発行する。
     *
     * @param username ユーザー名
     * @param password パスワード
     * @return ログインレスポンス（ユーザーID、トークン、ユーザー名）
     * @throws ApplicationException 認証失敗時
     */
    public LoginResponse login(String username, String password) {
        // 認証を実行
        UUID userId = authenticationDomainService.authenticate(username, password);
        if (userId == null) {
            throw new ApplicationException("Invalid username or password.");
        }

        // ユーザー情報を取得
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException("User not found."));

        // 新しいトークンを生成
        AuthToken authToken = tokenGenerationService.generateToken(user);
        authTokenRepository.save(authToken);

        return new LoginResponse(userId, authToken.getTokenValue(), user.getUsername());
    }
}
