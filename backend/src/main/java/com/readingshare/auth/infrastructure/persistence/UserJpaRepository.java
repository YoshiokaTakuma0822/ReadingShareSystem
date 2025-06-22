package com.readingshare.auth.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.readingshare.auth.domain.model.User;

/**
 * ユーザー情報のJPAリポジトリインターフェース。
 *
 * @author 003
 * @componentIdName C05 会員情報管理部
 * @moduleIdName M0512 ユーザーリポジトリ
 */
public interface UserJpaRepository extends JpaRepository<User, UUID> {

    /**
     * ユーザーIDでユーザーを検索する。
     *
     * @param id ユーザーID
     * @return ユーザーが見つかった場合はOptionalにUser、見つからない場合はOptional.empty()
     */
    @Override
    Optional<User> findById(UUID id);

    /**
     * ユーザー名でユーザーを検索する。
     *
     * @param username ユーザー名
     * @return ユーザーが見つかった場合はOptionalにUser、見つからない場合はOptional.empty()
     */
    Optional<User> findByUsername(String username);
}
