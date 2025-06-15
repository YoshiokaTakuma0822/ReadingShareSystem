package com.readingshare.auth.infrastructure.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * BCryptアルゴリズムを使用したパスワードハッシャーの実装。
 * Spring SecurityのBCryptPasswordEncoderを利用。
 * 担当: 小亀
 */
@Component
public class PasswordHasherImpl implements IPasswordHasher {
    private final BCryptPasswordEncoder passwordEncoder;

    public PasswordHasherImpl() {
        this.passwordEncoder = new BCryptPasswordEncoder();
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
