package com.readingshare.state.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ユーザープロフィールに関するAPI
 *
 * @author 02003
 * @componentId C4
 * @moduleName ユーザープロフィールコントローラー
 */
@RestController
@RequestMapping("/api/user")
public class UserProfileController {
    // ダミーユーザ情報
    private static final Map<String, Map<String, String>> USERS = new HashMap<>();
    static {
        USERS.put("user1", Map.of("userId", "user1", "displayName", "ユーザー1", "iconUrl", "/icons/user1.png"));
        USERS.put("user2", Map.of("userId", "user2", "displayName", "ユーザー2", "iconUrl", "/icons/user2.png"));
        USERS.put("user3", Map.of("userId", "user3", "displayName", "ユーザー3", "iconUrl", "/icons/user3.png"));
    }

    /**
     * ユーザープロフィールを取得する
     *
     * @param userId ユーザーID
     * @return ユーザープロフィール
     */
    @GetMapping("/{userId}/profile")
    public ResponseEntity<Map<String, String>> getProfile(@PathVariable String userId) {
        Map<String, String> profile = USERS.get(userId);
        if (profile == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(profile);
    }
}
