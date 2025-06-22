package com.readingshare.auth.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.auth.domain.model.AuthToken;
import com.readingshare.auth.domain.repository.IAuthTokenRepository;
import com.readingshare.common.exception.ApplicationException;

/**
 * ユーザーログアウトのアプリケーションサービス。
 *
 * @author 02003
 * @componentId C2
 * @moduleName ログアウトサービス
 * @see IAuthTokenRepository
 * @see AuthToken
 */
@Service
@Transactional
public class LogoutService {

    private final IAuthTokenRepository authTokenRepository;

    public LogoutService(IAuthTokenRepository authTokenRepository) {
        this.authTokenRepository = authTokenRepository;
    }

    /**
     * ユーザーをログアウトする（トークンを無効化）。
     *
     * @param tokenValue 無効化するトークン値
     * @throws ApplicationException トークンが見つからない場合
     */
    public void logout(String tokenValue) {
        Optional<AuthToken> authToken = authTokenRepository.findByTokenValue(tokenValue);

        if (authToken.isPresent()) {
            AuthToken token = authToken.get();
            token.deactivate();
            authTokenRepository.save(token);
        } else {
            throw new ApplicationException("Invalid token.");
        }
    }

    /**
     * ユーザーの全てのトークンを無効化する（全デバイスからログアウト）。
     *
     * @param tokenValue 現在のトークン値（ユーザー特定のため）
     * @throws ApplicationException トークンが見つからない場合
     */
    public void logoutAll(String tokenValue) {
        Optional<AuthToken> authToken = authTokenRepository.findByTokenValue(tokenValue);

        if (authToken.isPresent()) {
            AuthToken token = authToken.get();
            authTokenRepository.deactivateAllTokensByUserId(token.getUser().getId());
        } else {
            throw new ApplicationException("Invalid token.");
        }
    }
}
