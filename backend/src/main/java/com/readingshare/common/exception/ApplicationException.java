package com.readingshare.common.exception;

/**
 * アプリケーション層で発生する例外の基底クラス。
 * ユーザーへのフィードバックやログ出力に適した情報を保持する。
 */
public class ApplicationException extends RuntimeException {

    private final String code;

    public ApplicationException(String message) {
        this(null, message);
    }

    public ApplicationException(String message, Throwable cause) {
        this(null, message, cause);
    }

    /**
     * アプリケーションエラーコード付き例外
     */
    public ApplicationException(String code, String message) {
        super(message);
        this.code = code;
    }

    public ApplicationException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * エラーコードを取得
     */
    public String getCode() {
        return code;
    }
}
