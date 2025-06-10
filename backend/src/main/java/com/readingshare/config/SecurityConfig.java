package com.readingshare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import com.readingshare.security.CookieToHeaderAuthenticationFilter;
import com.readingshare.security.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final CustomUserDetailsService jwtUserDetailsService;
    private final CookieToHeaderAuthenticationFilter cookieToHeaderFilter;
    private final JwtDecoder jwtDecoder;

    public SecurityConfig(CustomUserDetailsService jwtUserDetailsService,
            CookieToHeaderAuthenticationFilter cookieToHeaderFilter,
            JwtDecoder jwtDecoder) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.cookieToHeaderFilter = cookieToHeaderFilter;
        this.jwtDecoder = jwtDecoder;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(null))
                .authorizeHttpRequests(authz -> authz
                        // Allow unauthenticated access to auth endpoints (login, register, refresh)
                        .requestMatchers("/api/accounts/login", "/api/accounts/register", "/api/accounts/refresh")
                        .permitAll()
                        // Allow authenticated access to logout and me (handled by JWT filters)
                        // .requestMatchers("/api/accounts/logout", "/api/accounts/me")
                        // .authenticated()
                        // Require authentication for other API endpoints
                        .requestMatchers("/api/**", "/ws/**").authenticated()
                        .anyRequest().permitAll())
                .userDetailsService(jwtUserDetailsService) // Use JWT UserDetailsService as default
                // Add custom filters in order of priority
                // .addFilterBefore(cookieToHeaderFilter,
                // UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(cookieToHeaderFilter, BearerTokenAuthenticationFilter.class)
                // Configure OAuth2 Resource Server for JWT authentication
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder)))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
