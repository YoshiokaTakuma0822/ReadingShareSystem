package com.readingshare.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.readingshare.account.domain.model.Account;
import com.readingshare.account.domain.repository.AccountRepository;
import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.repository.RoomRepository;

@Testcontainers
@SpringBootTest
public class RoomRepositoryIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    AccountRepository accountRepository;

    @Test
    void saveAndRetrieveRoom() {
        Account account = accountRepository.save(Account.create("user@example.com", "pass"));

        Room room = Room.create("test-room", "desc", account.getId());
        roomRepository.save(room);

        Optional<Room> loaded = roomRepository.findByName("test-room");
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getName()).isEqualTo("test-room");
        assertThat(roomRepository.existsByName("test-room")).isTrue();
    }
}
