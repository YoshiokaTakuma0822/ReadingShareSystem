package com.readingshare.auth.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.readingshare.auth.domain.model.AuthToken;

/**
 * 認証トークンのリポジトリインターフェース。
 *
 * @author 003
 * @componentId C2
 * @moduleName 認証トークンリポジトリ
 */
public interface IAuthTokenRepository {

    /**
     * トークンを保存する。
     *
     * @param token 保存するトークン
     * @return 保存されたトークン
     */
    AuthToken save(AuthToken token);

    /**
     * トークン値でトークンを検索する。
     *
     * @param tokenValue トークン値
     * @return 見つかったトークン
     */
    Optional<AuthToken> findByTokenValue(String tokenValue);

    /**
     * 有効なトークン値でトークンを検索する。
     *
     * @param tokenValue トークン値
     * @return 見つかった有効なトークン
     */
    Optional<AuthToken> findValidTokenByValue(String tokenValue);

    /**
     * ユーザーの全ての有効なトークンを取得する。
     *
     * @param userId ユーザーID
     * @return ユーザーの有効なトークンリスト
     */
    List<AuthToken> findActiveTokensByUserId(UUID userId);

    /**
     * ユーザーの全てのトークンを無効化する。
     *
     * @param userId ユーザーID
     */
    void deactivateAllTokensByUserId(UUID userId);

    /**
     * 期限切れのトークンを削除する。
     */
    void deleteExpiredTokens();
}
