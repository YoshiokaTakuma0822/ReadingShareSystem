package com.readingshare.auth.controller;

import com.readingshare.auth.service.LoginService;
import com.readingshare.auth.service.RegisterUserService;
import com.readingshare.auth.service.dto.LoginRequest;
import com.readingshare.auth.service.dto.RegisterUserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ログイン・会員登録に関するAPIを処理するコントローラー。
 * 担当: 小亀
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final LoginService loginService;
    private final RegisterUserService registerUserService;

    public AuthController(LoginService loginService, RegisterUserService registerUserService) {
        this.loginService = loginService;
        this.registerUserService = registerUserService;
    }

    /**
     * ユーザーログインを処理する。
     * @param request ログインリクエストデータ
     * @return 認証成功時はHTTP 200 OK
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        // 実際にはJWTなどの認証トークンを返す
        loginService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok("Login successful");
    }

    /**
     * 新規会員登録を処理する。
     * @param request 会員登録リクエストデータ
     * @return 登録成功時はHTTP 200 OK
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterUserRequest request) {
        registerUserService.register(request.getUsername(), request.getPassword());
        return ResponseEntity.ok("User registered successfully");
    }
}