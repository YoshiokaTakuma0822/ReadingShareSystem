package com.readingshare.auth.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.readingshare.auth.domain.model.User;
import com.readingshare.auth.domain.service.AuthenticationDomainService;
import com.readingshare.common.exception.ApplicationException;

/**
 * 新規会員登録のアプリケーションサービス。
 *
 * @author 003
 * @componentIdName C02 ログイン・会員登録
 * @moduleIdName M0222 新規会員登録サービス
 * @dependsOn M0210 認証ドメインサービス
 */
@Service
public class RegisterUserService {

    private final AuthenticationDomainService authenticationDomainService;

    public RegisterUserService(AuthenticationDomainService authenticationDomainService) {
        this.authenticationDomainService = authenticationDomainService;
    }

    /**
     * 新規ユーザーを登録する。
     *
     * @param username ユーザー名
     * @param password パスワード
     * @return 登録されたユーザーのID
     * @throws ApplicationException ユーザー名が既に存在する場合など
     */
    public UUID register(String username, String password) {
        // ドメインサービスにユーザー登録処理を委譲
        User newUser = new User(username, null, Instant.now()); // パスワードハッシュはドメインサービスで設定される, IDは自動生成
        User registeredUser = authenticationDomainService.registerUser(newUser, password);
        return registeredUser.getId();
    }
}
