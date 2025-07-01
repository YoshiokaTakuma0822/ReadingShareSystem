package com.readingshare.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * アプリケーション全体で使用するロガーユーティリティ。
 */
public class AppLogger {

    // 各クラスでLoggerインスタンスを取得するためのファクトリーメソッド
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    // 例: 特定の目的のためのロガー
    public static Logger getAccessLogger() {
        return LoggerFactory.getLogger("accessLog");
    }

    public static Logger getErrorLogger() {
        return LoggerFactory.getLogger("errorLog");
    }
}
