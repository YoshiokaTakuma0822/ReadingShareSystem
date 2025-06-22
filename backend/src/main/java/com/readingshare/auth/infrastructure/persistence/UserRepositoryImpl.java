package com.readingshare.auth.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.readingshare.auth.domain.model.User;
import com.readingshare.auth.domain.repository.IUserRepository;

/**
 * ユーザー情報のリポジトリ実装。
 *
 * @author 02003
 * @componentId C5
 * @moduleName ユーザーリポジトリ実装
 * @see UserJpaRepository
 */
@Repository
@Transactional
public class UserRepositoryImpl implements IUserRepository {
    private final UserJpaRepository userJpaRepository;

    public UserRepositoryImpl(UserJpaRepository userRepository) {
        this.userJpaRepository = userRepository;
    }

    /**
     * 指定されたユーザー情報を保存します。
     *
     * @param user 保存するユーザー情報
     * @return 保存されたユーザー情報
     */
    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    /**
     * ユーザーIDを基にユーザー情報を検索します。
     *
     * @param id 検索するユーザーID
     * @return 見つかった場合はユーザー情報を含むOptional、見つからない場合は空のOptional
     */
    @Override
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id);
    }

    /**
     * ユーザー名を基にユーザー情報を検索します。
     *
     * @param username 検索するユーザー名
     * @return 見つかった場合はユーザー情報を含むOptional、見つからない場合は空のOptional
     */
    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username);
    }
}
