package com.readingshare.auth.infrastructure.security;

/**
 * パスワードのハッシュ化と検証を行うインターフェース。
 * 担当: 小亀
 */
public interface IPasswordHasher {

    /**
     * 平文のパスワードをハッシュ化する。
     * 
     * @param rawPassword 平文のパスワード
     * @return ハッシュ化されたパスワード
     */
    String hashPassword(String rawPassword);

    /**
     * 平文のパスワードが与えられたハッシュと一致するか検証する。
     * 
     * @param rawPassword    平文のパスワード
     * @param hashedPassword ハッシュ化されたパスワード
     * @return 一致すればtrue、そうでなければfalse
     */
    boolean verifyPassword(String rawPassword, String hashedPassword);
}
