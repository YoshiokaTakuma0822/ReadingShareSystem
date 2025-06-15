package com.readingshare.auth.infrastructure.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readingshare.auth.domain.model.User;
import com.readingshare.auth.domain.model.UserId;

/**
 * ユーザー情報のJPAリポジトリインターフェース。
 * 担当: 小亀
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * ユーザーIDでユーザーを検索する。
     * 
     * @param id ユーザーID
     * @return ユーザーが見つかった場合はOptionalにUser、見つからない場合はOptional.empty()
     */
    Optional<User> findById(UserId id);

    /**
     * ユーザー名でユーザーを検索する。
     * 
     * @param username ユーザー名
     * @return ユーザーが見つかった場合はOptionalにUser、見つからない場合はOptional.empty()
     */
    Optional<User> findByUsername(String username);
}
