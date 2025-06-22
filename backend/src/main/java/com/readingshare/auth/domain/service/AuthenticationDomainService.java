package com.readingshare.auth.domain.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.auth.domain.model.User;
import com.readingshare.auth.domain.repository.IUserRepository;
import com.readingshare.auth.infrastructure.security.IPasswordHasher;
import com.readingshare.common.exception.DomainException;

/**
 * 認証とユーザー管理に関するドメインロジックを扱うサービス。
 *
 * @author 003
 * @componentId C2
 * @moduleName 認証ドメインサービス
 * @see IUserRepository
 */
@Service
public class AuthenticationDomainService {

    private final IUserRepository userRepository;
    private final IPasswordHasher passwordHasher;

    public AuthenticationDomainService(IUserRepository userRepository, IPasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    /**
     * ユーザーを認証する。
     *
     * @param username ユーザー名
     * @param password 平文のパスワード
     * @return 認証成功時はユーザーID、失敗時はnull
     * @throws DomainException データベースアクセスエラー時など
     */
    public UUID authenticate(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return null;
        }
        User user = userOptional.get();
        if (passwordHasher.verifyPassword(password, user.getPasswordHash())) {
            return user.getId();
        }
        return null;
    }

    /**
     * 新規ユーザーを登録する。
     *
     * @param user        登録するユーザーエンティティ（ID、パスワードハッシュは含まない）
     * @param rawPassword 平文のパスワード
     * @return 登録されたユーザーエンティティ
     * @throws DomainException ユーザー名が既に存在する場合
     */
    @Transactional
    public User registerUser(User user, String rawPassword) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new DomainException("Username '" + user.getUsername() + "' already exists.");
        }
        String hashedPassword = passwordHasher.hashPassword(rawPassword);
        user.setPasswordHash(hashedPassword);
        user.setJoinedAt(Instant.now()); // 登録日時を設定
        return userRepository.save(user);
    }
}
