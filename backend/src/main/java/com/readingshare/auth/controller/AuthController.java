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

/**
 * ログイン・会員登録に関するAPIを処理するコントローラー。
 * 担当: 小亀
 */
@RestController
@RequestMapping("/api/auth")
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
     * ユーザーログインを処理する。
     *
     * @param request ログインリクエストデータ
     * @return 認証成功時はベアラートークンとユーザー情報、HTTP 200 OK
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = loginService.login(request.username(), request.password());
        return ResponseEntity.ok(response);
    }

    /**
     * 新規会員登録を処理する。
     *
     * @param request 会員登録リクエストデータ
     * @return 登録成功時は新規ユーザーIDとHTTP 200 OK
     */
    @PostMapping("/register")
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
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // "Bearer " を除去
            logoutService.logout(token);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * 全デバイスからログアウトを処理する。
     *
     * @param authorizationHeader Authorizationヘッダー（Bearer Token）
     * @return ログアウト成功時はHTTP 200 OK
     */
    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // "Bearer " を除去
            logoutService.logoutAll(token);
        }
        return ResponseEntity.ok().build();
    }
}
