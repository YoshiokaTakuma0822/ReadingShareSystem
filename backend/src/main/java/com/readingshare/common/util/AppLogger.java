package com.readingshare.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * アプリケーション全体で使用するロガーユーティリティ。
 *
 * このクラスは、SLF4Jを使用したロギング機能を提供します。
 * 各クラスごとのロガーや、特定の目的（アクセスログ、エラーログ）の
 * ロガーを取得するためのファクトリーメソッドを提供しています。
 *
 * @author ReadingShare Team
 * @version 1.0
 * @since 1.0
 */
public class AppLogger {

    /**
     * 指定されたクラスのLoggerインスタンスを取得します。
     *
     * @param clazz ロガーを取得するクラス
     * @return 指定されたクラスのLoggerインスタンス
     * @throws NullPointerException clazzがnullの場合
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * アクセスログ専用のロガーを取得します。
     *
     * このロガーは、ユーザーのアクセス情報やAPIの呼び出し履歴を
     * 記録するために使用されます。
     *
     * @return アクセスログ専用のLoggerインスタンス
     */
    public static Logger getAccessLogger() {
        return LoggerFactory.getLogger("accessLog");
    }

    /**
     * エラーログ専用のロガーを取得します。
     *
     * このロガーは、システムエラーや例外情報を記録するために
     * 使用されます。
     *
     * @return エラーログ専用のLoggerインスタンス
     */
    public static Logger getErrorLogger() {
        return LoggerFactory.getLogger("errorLog");
    }
}
