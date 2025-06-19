package com.readingshare.auth.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readingshare.auth.infrastructure.security.UserPrincipal;

/**
 * 認証が必要なAPIのサンプルコントローラー。
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    /**
     * 現在認証されているユーザーの情報を取得する。
     *
     * @return ユーザー情報
     */
    @GetMapping("/me")
    public ResponseEntity<UserInfo> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            UserInfo userInfo = new UserInfo(
                    userPrincipal.getUserId(),
                    userPrincipal.getUsername());

            return ResponseEntity.ok(userInfo);
        }

        return ResponseEntity.notFound().build();
    }

    /**
     * ユーザー情報のレスポンスDTO。
     */
    public record UserInfo(UUID userId, String username) {
    }
}
