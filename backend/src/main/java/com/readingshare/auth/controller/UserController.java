package com.readingshare.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readingshare.auth.dto.UserInfo;
import com.readingshare.auth.infrastructure.security.UserPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 認証が必要なAPIのサンプルコントローラー。
 *
 * @author 02005
 * @componentId C2
 * @moduleName ユーザーコントローラー
 * @see UserInfo
 */
@RestController
@RequestMapping("/api/user")
@Tag(name = "ユーザー", description = "認証済みユーザー情報関連のAPI")
public class UserController {

    /**
     * 現在認証されているユーザーの情報を取得する。
     *
     * @return ユーザー情報
     */
    @GetMapping("/me")
    @Operation(summary = "現在のユーザー情報取得", description = "認証されたユーザーの情報を取得します")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ユーザー情報取得成功"),
            @ApiResponse(responseCode = "401", description = "認証が必要"),
            @ApiResponse(responseCode = "404", description = "ユーザーが見つからない")
    })
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
}
