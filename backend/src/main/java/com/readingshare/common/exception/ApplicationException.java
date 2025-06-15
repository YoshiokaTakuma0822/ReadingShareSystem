package com.readingshare.common.exception;

/**
 * アプリケーション層で発生する例外の基底クラス。
 * ユーザーへのフィードバックやログ出力に適した情報を保持する。
 */
public class ApplicationException extends RuntimeException {

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}