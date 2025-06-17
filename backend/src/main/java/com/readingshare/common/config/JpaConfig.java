package com.readingshare.common.config;

import org.springframework.context.annotation.Configuration;

/**
 * JPA設定クラス
 * Hibernate 6.4以降では@JdbcTypeCodeでJSONB型を自動処理
 */
@Configuration
public class JpaConfig {
    // Hibernate 6.4以降では@JdbcTypeCode(SqlTypes.JSON)により
    // JSONB型の自動変換がサポートされる
}
