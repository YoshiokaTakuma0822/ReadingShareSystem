package com.readingshare.auth.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.readingshare.auth.domain.service.AuthenticationDomainService;
import com.readingshare.common.exception.ApplicationException;

/**
 * ユーザーログインのアプリケーションサービス。
 * 担当: 小亀
 */
@Service
public class LoginService {

    private final AuthenticationDomainService authenticationDomainService;

    public LoginService(AuthenticationDomainService authenticationDomainService) {
        this.authenticationDomainService = authenticationDomainService;
    }

    /**
     * ユーザー認証を行う。
     * 
     * @param username ユーザー名
     * @param password パスワード
     * @return 認証されたユーザーのID
     * @throws ApplicationException 認証失敗時
     */
    public UUID login(String username, String password) {
        UUID userId = authenticationDomainService.authenticate(username, password);
        if (userId == null) {
            throw new ApplicationException("Invalid username or password.");
        }
        // 認証成功後の追加処理（例: セッション管理、JWT生成など）
        return userId;
    }
}
