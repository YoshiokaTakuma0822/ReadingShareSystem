package com.readingshare.common.exception;

/**
 * リソースが見つからない場合に発生する例外。
 * HTTP 404 Not Found として処理される。
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
