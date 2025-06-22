package com.readingshare.auth.infrastructure.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.auth.domain.model.AuthToken;
import com.readingshare.auth.domain.repository.IAuthTokenRepository;

/**
 * 認証トークンリポジトリの実装。
 *
 * @author 003
 * @componentId C5
 * @moduleName 認証トークンリポジトリ実装
 * @see AuthTokenJpaRepository
 */
@Repository
@Transactional
public class AuthTokenRepositoryImpl implements IAuthTokenRepository {

    private final AuthTokenJpaRepository jpaRepository;

    public AuthTokenRepositoryImpl(AuthTokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    /**
     * 指定された認証トークンを保存します。
     *
     * @param token 保存する認証トークン
     * @return 保存された認証トークン
     */
    @Override
    public AuthToken save(AuthToken token) {
        return jpaRepository.save(token);
    }

    /**
     * トークンの値を基に認証トークンを検索します。
     *
     * @param tokenValue 検索するトークンの値
     * @return 見つかった場合は認証トークンを含むOptional、見つからない場合は空のOptional
     */
    @Override
    public Optional<AuthToken> findByTokenValue(String tokenValue) {
        return jpaRepository.findByTokenValue(tokenValue);
    }

    /**
     * トークンの値を基に有効な認証トークンを検索します。
     *
     * @param tokenValue 検索するトークンの値
     * @return 見つかった場合は有効な認証トークンを含むOptional、見つからない場合は空のOptional
     */
    @Override
    public Optional<AuthToken> findValidTokenByValue(String tokenValue) {
        return jpaRepository.findValidTokenByValue(tokenValue, Instant.now());
    }

    /**
     * 指定されたユーザーIDに関連付けられたすべての有効な認証トークンを検索します。
     *
     * @param userId ユーザーのID
     * @return ユーザーに関連付けられた有効な認証トークンのリスト
     */
    @Override
    public List<AuthToken> findActiveTokensByUserId(UUID userId) {
        return jpaRepository.findActiveTokensByUserId(userId, Instant.now());
    }

    /**
     * 指定されたユーザーIDに関連付けられたすべての認証トークンを無効化します。
     *
     * @param userId ユーザーのID
     */
    @Override
    public void deactivateAllTokensByUserId(UUID userId) {
        jpaRepository.deactivateAllTokensByUserId(userId);
    }

    /**
     * 期限切れの認証トークンをすべて削除します。
     */
    @Override
    public void deleteExpiredTokens() {
        jpaRepository.deleteExpiredTokens(Instant.now());
    }
}
