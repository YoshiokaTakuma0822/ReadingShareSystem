package com.readingshare.auth.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readingshare.auth.dto.LoginRequest;
import com.readingshare.auth.dto.LoginResponse;
import com.readingshare.auth.dto.RegisterUserRequest;
import com.readingshare.auth.service.LoginService;
import com.readingshare.auth.service.LogoutService;
import com.readingshare.auth.service.RegisterUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * ログイン・会員登録に関するAPIを処理するコントローラー。
 *
 * @author 02005
 * @componentId C2
 * @moduleName ログイン・会員登録コントローラー
 * @see LoginService
 * @see RegisterUserService
 * @see LogoutService
 * @see LoginRequest
 * @see LoginResponse
 * @see RegisterUserRequest
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "認証", description = "ユーザー認証関連のAPI")
public class AuthController {
    private final LoginService loginService;
    private final RegisterUserService registerUserService;
    private final LogoutService logoutService;

    public AuthController(LoginService loginService, RegisterUserService registerUserService,
            LogoutService logoutService) {
        this.loginService = loginService;
        this.registerUserService = registerUserService;
        this.logoutService = logoutService;
    }

    /**
     * ユーザーログインを処理する
     *
     * @param request ログインリクエストデータ
     * @return 認証成功時はベアラートークンとユーザー情報、HTTP 200 OK
     * @throws RuntimeException 認証失敗時は400 Bad Request
     */
    @PostMapping("/login")
    @Operation(summary = "ユーザーログイン", description = "ユーザー名とパスワードでログインし、Bearer Tokenを取得します")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ログイン成功"),
            @ApiResponse(responseCode = "400", description = "認証失敗")
    })
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = loginService.login(request.username(), request.password());
        return ResponseEntity.ok(response);
    }

    /**
     * 新規会員登録を処理する。
     *
     * @param request 会員登録リクエストデータ
     * @return 登録成功時は新規ユーザーIDとHTTP 200 OK
     * @throws RuntimeException 登録失敗時に400 Bad Request
     */
    @PostMapping("/register")
    @Operation(summary = "新規会員登録", description = "新しいユーザーアカウントを作成します")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登録成功"),
            @ApiResponse(responseCode = "400", description = "登録失敗（ユーザー名重複など）")
    })
    public ResponseEntity<UUID> register(@RequestBody RegisterUserRequest request) {
        UUID userId = registerUserService.register(request.username(), request.password());
        return ResponseEntity.ok(userId);
    }

    /**
     * ユーザーログアウトを処理する。
     *
     * @param authorizationHeader Authorizationヘッダー（Bearer Token）
     * @return ログアウト成功時はHTTP 200 OK
     */
    @PostMapping("/logout")
    @Operation(summary = "ログアウト", description = "現在のデバイスからログアウトします")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ログアウト成功")
    })
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // "Bearer " を除去
            logoutService.logout(token);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * 全デバイスからログアウトを処理する
     *
     * @param authorizationHeader Authorizationヘッダー（Bearer Token）
     * @return ログアウト成功時はHTTP 200 OK
     */
    @PostMapping("/logout-all")
    @Operation(summary = "全デバイスからログアウト", description = "すべてのデバイスからログアウトします")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ログアウト成功")
    })
    public ResponseEntity<Void> logoutAll(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // "Bearer " を除去
            logoutService.logoutAll(token);
        }
        return ResponseEntity.ok().build();
    }
}
