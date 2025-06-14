package com.readingshare.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * 日付と時刻の変換ユーティリティ。
 */
public class DateConverter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Instantを特定のフォーマットの文字列に変換する。
     * @param instant 変換するInstantオブジェクト
     * @return フォーマットされた日付時刻文字列
     */
    public static String formatInstant(Instant instant) {
        if (instant == null) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC).format(FORMATTER);
    }

    /**
     * フォーマットされた文字列をInstantに変換する。
     * @param dateTimeString フォーマットされた日付時刻文字列
     * @return 変換されたInstantオブジェクト
     * @throws java.time.format.DateTimeParseException パースに失敗した場合
     */
    public static Instant parseInstant(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeString, FORMATTER).toInstant(ZoneOffset.UTC);
    }

    // 他にも、Long (UNIXタイムスタンプ) との変換など、必要に応じてメソッドを追加
}