package com.readingshare.account.controller;

import java.util.UUID;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readingshare.account.domain.model.Account;
import com.readingshare.account.dto.AccountLoginRequest;
import com.readingshare.account.dto.AccountMeResponse;
import com.readingshare.account.dto.AccountRegisterRequest;
import com.readingshare.account.dto.AccountRegisterResponse;
import com.readingshare.account.exception.EmailAlreadyExistsException;
import com.readingshare.account.service.AccountService;
import com.readingshare.security.JwtPayload;
import com.readingshare.security.JwtService;

/**
 * REST controller for account-related endpoints.
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final JwtService jwtService;
    private final JwtDecoder jwtDecoder;
    private final AccountService accountService;
    private final AuthenticationManager authenticationManager;

    public AccountController(
            JwtService jwtService,
            JwtDecoder jwtDecoder,
            AccountService accountService,
            AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.jwtDecoder = jwtDecoder;
        this.accountService = accountService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Register a new account with a user.
     *
     * Note: attempting to register with an email that already exists will result in
     * a 409 Conflict error.
     * Consider verifying email availability on the client side before sending the
     * registration request.
     *
     * @param registerRequest the account registration data
     * @return the created account information
     */
    @PostMapping("/register")
    public @NonNull ResponseEntity<?> register(@Validated @RequestBody AccountRegisterRequest registerRequest) {
        if (accountService.findByEmail(registerRequest.email()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        var createdAccount = accountService.createAccount(registerRequest.email(), registerRequest.password());

        // Return account information in a format consistent with the frontend
        // expectations
        return ResponseEntity.ok(new AccountRegisterResponse(
                createdAccount.getId(),
                createdAccount.getEmail()));
    }

    /**
     * Authenticate account and return JWT token.
     *
     * Note: invalid credentials will result in a 401 Unauthorized response.
     * Ensure correct email and password are provided before attempting to login.
     *
     * @param loginRequest the login credentials
     * @return JWT token and user information
     */
    @PostMapping("/login")
    public @NonNull ResponseEntity<?> login(@Validated @RequestBody AccountLoginRequest loginRequest) {
        try {
            // Authenticate user using Spring Security's AuthenticationManager
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));

            // Get the account entity for token generation
            var account = accountService.findByEmail(loginRequest.email())
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            // Generate token pair using account entity
            var tokenPair = jwtService.generateTokenPair(account);

            // Store refresh token in account DB for one-to-one mapping
            accountService.updateRefreshToken(account.getId(), tokenPair.refreshToken().getTokenValue());

            // Set HttpOnly cookie for web clients
            ResponseCookie jwtCookie = ResponseCookie
                    .from("jwt-token", tokenPair.accessToken().getTokenValue())
                    .httpOnly(true)
                    .secure(false) // Set to true in production with HTTPS
                    .path("/")
                    .maxAge(jwtService.getJwtExpiration() / 1000)
                    .sameSite("Lax")
                    .build();
            ResponseCookie refreshCookie = ResponseCookie
                    .from("refresh-token", tokenPair.refreshToken().getTokenValue())
                    .httpOnly(true)
                    .secure(false)
                    .path("/api/accounts/refresh")
                    .maxAge(jwtService.getRefreshExpiration() / 1000)
                    .sameSite("Lax")
                    .build();
            // Add access token expiration time cookie for frontend
            ResponseCookie expireCookie = ResponseCookie
                    .from("jwt-expires-at", tokenPair.accessToken().getExpiresAt().toString())
                    .httpOnly(false) // Allow frontend access
                    .secure(false)
                    .path("/")
                    .maxAge(jwtService.getJwtExpiration() / 1000)
                    .sameSite("Lax")
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, expireCookie.toString())
                    .body("Login successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    /**
     * Refresh access token using refresh token cookie.
     */
    @PostMapping("/refresh")
    public @NonNull ResponseEntity<?> refreshToken(@CookieValue(value = "refresh-token") String refreshToken) {
        // First check if the refresh token exists in the database
        var jwt = jwtDecoder.decode(refreshToken); // Validate format
        var payload = JwtPayload.fromJwt(jwt);

        var account = accountService.getAccount(UUID.fromString(payload.sub())).get();
        if (!refreshToken.equals(account.getRefreshToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        // Check if token was refreshed recently (within 5 minutes) to prevent too
        // frequent refreshes
        if (account.wasTokenRefreshedRecently(5)) {
            return ResponseEntity.ok().body("Token was refreshed recently, skipping refresh");
        }

        // Generate new access token using account entity
        var newToken = jwtService.generateToken(account);

        accountService.updateRefreshToken(account.getId(), account.getRefreshToken());

        var jwtCookieNew = ResponseCookie.from("jwt-token", newToken.getTokenValue())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(jwtService.getJwtExpiration() / 1000)
                .sameSite("Lax")
                .build();
        // Add access token expiration time cookie for frontend
        var expireCookie = ResponseCookie.from("jwt-expires-at", newToken.getExpiresAt().toString())
                .httpOnly(false) // Allow frontend access
                .secure(false)
                .path("/")
                .maxAge(jwtService.getJwtExpiration() / 1000)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookieNew.toString())
                .header(HttpHeaders.SET_COOKIE, expireCookie.toString())
                .body("Token refreshed successfully");
    }

    /**
     * Logout user by clearing the JWT cookie.
     *
     * @return success message
     */
    @PostMapping("/logout")
    public @NonNull ResponseEntity<?> logout(@AuthenticationPrincipal Jwt jwt) {
        // Clear the JWT cookie
        ResponseCookie jwtCookie = ResponseCookie.from("jwt-token", "")
                .httpOnly(true)
                .secure(false) // Set to true in production with HTTPS
                .path("/")
                .maxAge(0) // Expire immediately
                .sameSite("Lax")
                .build();

        // Clear the refresh token cookie and remove from DB
        var payload = JwtPayload.fromJwt(jwt);
        // Get account by UUID from the JWT subject
        var account = accountService.getAccount(UUID.fromString(payload.sub())).orElse(null);
        if (account != null) {
            accountService.clearRefreshToken(account.getId());
        }

        ResponseCookie refreshCookie = ResponseCookie.from("refresh-token", "")
                .httpOnly(true)
                .secure(false)
                .path("/api/accounts/refresh")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        // Clear the access token expiration cookie
        ResponseCookie expireCookie = ResponseCookie.from("jwt-expires-at", "")
                .httpOnly(false)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .header(HttpHeaders.SET_COOKIE, expireCookie.toString())
                .body("Logged out successfully");
    }

    /**
     * Get current authenticated user information.
     */
    @GetMapping("/me")
    public @NonNull ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        try {
            var payload = JwtPayload.fromJwt(jwt);
            Account account = accountService.getAccount(UUID.fromString(payload.sub()))
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            return ResponseEntity.ok(new AccountMeResponse(
                    account.getId(),
                    account.getEmail(),
                    jwt.getExpiresAt()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
    }
}
