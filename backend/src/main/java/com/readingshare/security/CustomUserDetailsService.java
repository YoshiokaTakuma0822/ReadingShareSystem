package com.readingshare.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.readingshare.account.service.AccountService;

/**
 * Custom UserDetailsService for JWT-based authentication.
 * This service is used by JWT authentication filters for validating tokens
 * and providing user details for authenticated JWT requests.
 *
 * Difference from LoginUserDetailsService:
 * - Used for JWT token validation and authorization
 * - Returns standard Spring User implementation
 * - Optimized for repeated authentication checks during JWT validation
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final AccountService accountService;

    public CustomUserDetailsService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var account = accountService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return User.builder()
                .username(account.getEmail())
                .password(account.getPassword())
                .authorities("ROLE_USER") // Default role for all users
                .build();
    }
}
