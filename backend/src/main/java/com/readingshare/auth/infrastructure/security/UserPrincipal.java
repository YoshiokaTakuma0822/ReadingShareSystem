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
 */
public class UserPrincipal implements UserDetails {

    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 基本的なユーザー権限を付与
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * ユーザーIDを取得する。
     *
     * @return ユーザーID
     */
    public UUID getUserId() {
        return user.getId();
    }

    /**
     * Userエンティティを取得する。
     *
     * @return Userエンティティ
     */
    public User getUser() {
        return user;
    }
}
