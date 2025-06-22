package com.readingshare.auth.infrastructure.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Spring SecurityのPasswordEncoderを使用したパスワードハッシャーの実装。
 *
 * @author 003
 * @componentId C5
 * @moduleName パスワードハッシュ化実装
 * @see IPasswordHasher
 */
@Component
public class PasswordHasherImpl implements IPasswordHasher {

    private final PasswordEncoder passwordEncoder;

    /**
     * コンストラクタ。
     *
     * @param passwordEncoder Spring SecurityのPasswordEncoder
     */
    public PasswordHasherImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 生のパスワードをハッシュ化します。
     *
     * @param rawPassword ハッシュ化する生のパスワード
     * @return ハッシュ化されたパスワード
     */
    @Override
    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 生のパスワードとハッシュ化されたパスワードを検証します。
     *
     * @param rawPassword    検証する生のパスワード
     * @param hashedPassword 検証するハッシュ化されたパスワード
     * @return パスワードが一致する場合はtrue、それ以外はfalse
     */
    @Override
    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}
