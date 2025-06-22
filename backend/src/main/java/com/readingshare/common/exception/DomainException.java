package com.readingshare.common.exception;

/**
 * ドメイン層で発生する例外の基底クラス。
 * ビジネスルール違反など、ドメイン固有の制約によって発生する。
 * 通常、上位層で捕捉され、ApplicationExceptionにラップされるか、直接処理される。
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
