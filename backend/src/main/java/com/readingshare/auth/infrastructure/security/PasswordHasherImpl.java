package com.readingshare.auth.infrastructure.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Spring SecurityのPasswordEncoderを使用したパスワードハッシャーの実装。
 * 担当: 小亀
 */
@Component
public class PasswordHasherImpl implements IPasswordHasher {

    private final PasswordEncoder passwordEncoder;

    public PasswordHasherImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}
