package com.readingshare.auth.infrastructure.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.readingshare.auth.domain.repository.IAuthTokenRepository;

/**
 * 認証トークンの定期的なクリーンアップを行うスケジュールタスク。
 *
 * @author 003
 * @componentIdName C05 会員情報管理部
 * @moduleIdName M0517 認証トークンクリーンアップスケジューラー
 */
@Component
public class TokenCleanupScheduler {

    private static final Logger logger = LoggerFactory.getLogger(TokenCleanupScheduler.class);

    private final IAuthTokenRepository authTokenRepository;

    public TokenCleanupScheduler(IAuthTokenRepository authTokenRepository) {
        this.authTokenRepository = authTokenRepository;
    }

    /**
     * 期限切れトークンを定期的にクリーンアップする。
     * 毎日午前2時に実行される。
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupExpiredTokens() {
        logger.info("Starting cleanup of expired tokens...");
        try {
            authTokenRepository.deleteExpiredTokens();
            logger.info("Completed cleanup of expired tokens.");
        } catch (Exception e) {
            logger.error("Error during token cleanup", e);
        }
    }
}
