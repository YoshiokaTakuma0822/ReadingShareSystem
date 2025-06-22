package com.readingshare.auth.infrastructure.security;

/**
 * パスワードのハッシュ化と検証を行うインターフェース。
 *
 * @author 003
 * @componentIdName C05 会員情報管理部
 * @moduleIdName M0522 パスワードハッシュ化インターフェース
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
