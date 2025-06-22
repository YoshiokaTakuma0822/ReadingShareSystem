package com.readingshare.auth.infrastructure.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.readingshare.auth.domain.model.AuthToken;

/**
 * 認証トークンのJPAリポジトリ。
 *
 * @author 003
 * @componentId C5
 * @moduleName 認証トークンリポジトリ
 */
public interface AuthTokenJpaRepository extends JpaRepository<AuthToken, UUID> {

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
     * @param now        現在時刻
     * @return 見つかった有効なトークン
     */
    @Query("SELECT t FROM AuthToken t WHERE t.tokenValue = :tokenValue AND t.isActive = true AND t.expiresAt > :now")
    Optional<AuthToken> findValidTokenByValue(@Param("tokenValue") String tokenValue, @Param("now") Instant now);

    /**
     * ユーザーの全ての有効なトークンを取得する。
     *
     * @param userId ユーザーID
     * @param now    現在時刻
     * @return ユーザーの有効なトークンリスト
     */
    @Query("SELECT t FROM AuthToken t WHERE t.user.id = :userId AND t.isActive = true AND t.expiresAt > :now")
    List<AuthToken> findActiveTokensByUserId(@Param("userId") UUID userId, @Param("now") Instant now);

    /**
     * ユーザーの全てのトークンを無効化する。
     *
     * @param userId ユーザーID
     */
    @Modifying
    @Query("UPDATE AuthToken t SET t.isActive = false WHERE t.user.id = :userId")
    void deactivateAllTokensByUserId(@Param("userId") UUID userId);

    /**
     * 期限切れのトークンを削除する。
     *
     * @param now 現在時刻
     */
    @Modifying
    @Query("DELETE FROM AuthToken t WHERE t.expiresAt < :now OR t.isActive = false")
    void deleteExpiredTokens(@Param("now") Instant now);
}
