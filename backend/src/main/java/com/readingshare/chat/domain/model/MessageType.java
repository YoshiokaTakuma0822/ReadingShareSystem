package com.readingshare.chat.domain.model;

/**
 * チャットメッセージのタイプを定義する定数クラス
 */
public final class MessageType {

    /** 通常のテキストメッセージ */
    public static final String TEXT = "TEXT";

    /** アンケートメッセージ */
    public static final String SURVEY = "SURVEY";

    /** システムメッセージ（部屋参加通知など） */
    public static final String SYSTEM = "SYSTEM";

    private MessageType() {
        // インスタンス化を防ぐ
    }
}
