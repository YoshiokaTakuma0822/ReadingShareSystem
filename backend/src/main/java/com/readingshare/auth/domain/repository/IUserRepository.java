package com.readingshare.auth.domain.repository;

import com.readingshare.auth.domain.model.User;
import com.readingshare.auth.domain.model.UserId;

import java.util.Optional;

/**
 * ユーザー情報の永続化を担当するリポジトリインターフェース。
 * 担当: 小亀
 */
public interface IUserRepository {

    /**
     * ユーザーを保存する。
     * @param user 保存するユーザーエンティティ
     * @return 保存されたユーザーエンティティ
     */
    User save(User user);

    /**
     * ユーザーIDでユーザーを検索する。
     * @param id ユーザーID
     * @return ユーザーが見つかった場合はOptionalにUser、見つからない場合はOptional.empty()
     */
    Optional<User> findById(UserId id);

    /**
     * ユーザー名でユーザーを検索する。
     * @param username ユーザー名
     * @return ユーザーが見つかった場合はOptionalにUser、見つからない場合はOptional.empty()
     */
    Optional<User> findByUsername(String username);
}