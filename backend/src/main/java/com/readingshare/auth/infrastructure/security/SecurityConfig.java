package com.readingshare.auth.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Securityの設定クラス。
 *
 * @author 003
 * @componentIdName C05 会員情報管理部
 * @moduleIdName M0524 セキュリティ設定
 * @dependsOn M0514 Bearer Token認証フィルター
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final BearerTokenAuthenticationFilter bearerTokenAuthenticationFilter;

    /**
     * コンストラクタ。
     *
     * @param bearerTokenAuthenticationFilter Bearer Token認証フィルター
     */
    public SecurityConfig(BearerTokenAuthenticationFilter bearerTokenAuthenticationFilter) {
        this.bearerTokenAuthenticationFilter = bearerTokenAuthenticationFilter;
    }

    /**
     * セキュリティフィルタチェーンを構成します。
     *
     * @param http HttpSecurityオブジェクト
     * @return 構成されたSecurityFilterChain
     * @throws Exception 構成中にエラーが発生した場合
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // REST APIのためCSRFを無効化
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // ステートレス
                .authorizeHttpRequests(authz -> authz
                        // 認証不要のエンドポイント
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .requestMatchers("/api/auth/logout").permitAll()
                        // Swagger UI関連
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        // 健康チェック
                        .requestMatchers("/actuator/health").permitAll()
                        // その他のAPIは認証が必要
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll())
                .addFilterBefore(bearerTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * パスワードエンコーダーを提供します。
     *
     * @return BCryptPasswordEncoderのインスタンス
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
