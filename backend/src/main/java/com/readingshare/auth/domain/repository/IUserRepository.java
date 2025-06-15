package com.readingshare.auth.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.readingshare.auth.domain.model.User;

/**
 * ユーザー情報の永続化を担当するリポジトリインターフェース。
 * 担当: 小亀
 */
public interface IUserRepository {

    /**
     * ユーザーを保存する。
     *
     * @param user 保存するユーザーエンティティ
     * @return 保存されたユーザーエンティティ
     */
    User save(User user);

    /**
     * ユーザーIDでユーザーを検索する。
     *
     * @param id ユーザーID（UUID）
     * @return ユーザーが見つかった場合はOptionalにUser、見つからない場合はOptional.empty()
     */
    Optional<User> findById(UUID id);

    /**
     * ユーザー名でユーザーを検索する。
     *
     * @param username ユーザー名
     * @return ユーザーが見つかった場合はOptionalにUser、見つからない場合はOptional.empty()
     */
    Optional<User> findByUsername(String username);
}
