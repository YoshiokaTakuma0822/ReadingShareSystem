package com.readingshare.auth.service;

import com.readingshare.auth.domain.model.User;
import com.readingshare.auth.domain.service.AuthenticationDomainService;
import com.readingshare.common.exception.ApplicationException;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * 新規会員登録のアプリケーションサービス。
 * 担当: 小亀
 */
@Service
public class RegisterUserService {

    private final AuthenticationDomainService authenticationDomainService;

    public RegisterUserService(AuthenticationDomainService authenticationDomainService) {
        this.authenticationDomainService = authenticationDomainService;
    }

    /**
     * 新規ユーザーを登録する。
     * @param username ユーザー名
     * @param password パスワード
     * @throws ApplicationException ユーザー名が既に存在する場合など
     */
    public void register(String username, String password) {
        // ドメインサービスにユーザー登録処理を委譲
        User newUser = new User((Long)null, username, null, Instant.now()); // IDとcontentsはドメインサービスで設定されるか、後で更新
        authenticationDomainService.registerUser(newUser, password);
    }
}