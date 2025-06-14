package com.readingshare.common.config;

/**
 * アプリケーション全体で使用される定数を定義するクラス。
 */
public final class AppConstants {

    private AppConstants() {
        // インスタンス化を防ぐためのプライベートコンストラクタ
    }

    // 認証関連
    public static final String AUTH_TOKEN_HEADER = "Authorization";
    public static final String AUTH_TOKEN_PREFIX = "Bearer ";

    // 部屋関連
    public static final int ROOM_NAME_MAX_LENGTH = 100;
    public static final int BOOK_TITLE_MAX_LENGTH = 200;

    // チャット関連
    public static final int MESSAGE_CONTENT_MAX_LENGTH = 1000;
    public static final int CHAT_HISTORY_LIMIT = 50; // 一度に取得するチャット履歴の最大件数

    // アンケート関連
    public static final int SURVEY_TITLE_MAX_LENGTH = 255;
    public static final int QUESTION_TEXT_MAX_LENGTH = 500;

    // ページングデフォルト値
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_NUMBER = 0;

    // その他の共通定数
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
}