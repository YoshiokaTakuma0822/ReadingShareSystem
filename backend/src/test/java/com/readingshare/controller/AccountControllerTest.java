package com.readingshare.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readingshare.account.domain.model.Account;
import com.readingshare.account.dto.AccountLoginRequest;
import com.readingshare.account.infrastructure.repository.AccountJpaRepository;
import com.readingshare.account.service.AccountService;

public class AccountControllerTest extends ControllerIntegrationTestBase {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountJpaRepository accountJpaRepository;

    private Account testUser;

    @BeforeEach
    void setUp() {
        String email = "user-" + UUID.randomUUID() + "@example.com";
        testUser = accountService.createAccount(email, "password");
    }

    @Test
    void whenLogin_thenCanAccessMe() throws Exception {
        var login = new AccountLoginRequest(testUser.getEmail(), "password");
        MvcResult loginResult = mockMvc.perform(post("/api/accounts/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        var jwtCookie = loginResult.getResponse().getCookie("jwt-token");
        assertThat(jwtCookie).isNotNull();

        mockMvc.perform(get("/api/accounts/me").cookie(jwtCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(testUser.getEmail()));
    }

    @Test
    void whenRefresh_thenGetNewAccessToken() throws Exception {
        // Create a unique user for this test to ensure fresh token state
        String uniqueEmail = "refresh-test-" + UUID.randomUUID() + "@example.com";
        Account refreshTestUser = accountService.createAccount(uniqueEmail, "password");

        var login = new AccountLoginRequest(refreshTestUser.getEmail(), "password");
        MvcResult loginResult = mockMvc.perform(post("/api/accounts/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andReturn();

        // Hack: Force reset the lastTokenRefreshAt to ensure refresh will always
        // succeed
        // Directly manipulate the database entity to set lastTokenRefreshAt to null
        var entity = accountJpaRepository.findById(refreshTestUser.getId()).orElseThrow();
        entity.setLastTokenRefreshAt(null);
        accountJpaRepository.save(entity);

        var refreshCookie = loginResult.getResponse().getCookie("refresh-token");
        assertThat(refreshCookie).isNotNull();

        MvcResult refreshResult = mockMvc.perform(post("/api/accounts/refresh").cookie(refreshCookie))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = refreshResult.getResponse().getContentAsString();
        // Since we're using a fresh user, the refresh should always succeed
        assertThat(responseBody).isEqualTo("Token refreshed successfully");

        // Verify that new cookies were set
        assertThat(refreshResult.getResponse().getCookie("jwt-token")).isNotNull();
        assertThat(refreshResult.getResponse().getCookie("jwt-expires-at")).isNotNull();
    }

    @Test
    void whenRefreshTwiceQuickly_thenSecondRefreshIsSkipped() throws Exception {
        // Create a unique user for this test to ensure fresh token state
        String uniqueEmail = "double-refresh-test-" + UUID.randomUUID() + "@example.com";
        Account doubleRefreshTestUser = accountService.createAccount(uniqueEmail, "password");

        var login = new AccountLoginRequest(doubleRefreshTestUser.getEmail(), "password");
        MvcResult loginResult = mockMvc.perform(post("/api/accounts/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andReturn();

        var refreshCookie = loginResult.getResponse().getCookie("refresh-token");
        assertThat(refreshCookie).isNotNull();

        // First refresh - should succeed
        mockMvc.perform(post("/api/accounts/refresh").cookie(refreshCookie))
                .andExpect(status().isOk())
                .andReturn();

        // Second refresh immediately after - should be skipped
        MvcResult secondRefreshResult = mockMvc.perform(post("/api/accounts/refresh").cookie(refreshCookie))
                .andExpect(status().isOk())
                .andReturn();

        String secondResponseBody = secondRefreshResult.getResponse().getContentAsString();
        assertThat(secondResponseBody).isEqualTo("Token was refreshed recently, skipping refresh");
    }
}
