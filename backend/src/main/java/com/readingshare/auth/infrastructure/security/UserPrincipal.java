package com.readingshare.auth.infrastructure.security;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.readingshare.auth.domain.model.User;

/**
 * Spring SecurityのUserDetailsを実装するユーザープリンシパル。
 *
 * @author 02005
 * @componentId C5
 * @moduleName ユーザープリンシパル
 */
public class UserPrincipal implements UserDetails {

    private final User user;

    /**
     * コンストラクタ。
     *
     * @param user Userエンティティ
     */
    public UserPrincipal(User user) {
        this.user = user;
    }

    /**
     * ユーザーの権限を取得します。
     *
     * @return ユーザーのGrantedAuthorityのコレクション
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 基本的なユーザー権限を付与
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /**
     * ユーザーのパスワードを取得します。
     *
     * @return ユーザーのパスワードハッシュ
     */
    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    /**
     * ユーザー名を取得します。
     *
     * @return ユーザー名
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * アカウントが期限切れでないかを確認します。
     *
     * @return 常にtrueを返します
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * アカウントがロックされていないかを確認します。
     *
     * @return 常にtrueを返します
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 資格情報が期限切れでないかを確認します。
     *
     * @return 常にtrueを返します
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * アカウントが有効かどうかを確認します。
     *
     * @return 常にtrueを返します
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * ユーザーIDを取得します。
     *
     * @return ユーザーID
     */
    public UUID getUserId() {
        return user.getId();
    }

    /**
     * Userエンティティを取得します。
     *
     * @return Userエンティティ
     */
    public User getUser() {
        return user;
    }
}
