package com.readingshare.common.exception;

/**
 * ユーザー名重複時の例外。
 */
public class DuplicateUsernameException extends ApplicationException {

    private static final String CODE = "USERNAME_ALREADY_EXISTS";

    public DuplicateUsernameException(String username) {
        super(CODE, "Username '" + username + "' already exists.");
    }
}
