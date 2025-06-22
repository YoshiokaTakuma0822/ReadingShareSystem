package com.readingshare.common.exception;

/**
 * データベースアクセス層で発生する例外。
 * 永続化操作の失敗時にスローされる。
 */
public class DatabaseAccessException extends ApplicationException {

    public DatabaseAccessException(String message) {
        super(message);
    }

    public DatabaseAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
