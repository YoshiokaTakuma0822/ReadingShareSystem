package com.readingshare.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.readingshare.account.domain.model.Account;
import com.readingshare.account.domain.service.AccountDomainService;
import com.readingshare.account.service.AccountService;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountDomainService accountDomainService;

    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccount_delegatesToDomainService() {
        Account account = Account.create("a", "encoded");
        when(accountDomainService.createAccount("a", "p")).thenReturn(account);

        Account result = accountService.createAccount("a", "p");

        assertThat(result).isEqualTo(account);
        verify(accountDomainService).createAccount("a", "p");
    }

    @Test
    void updateRefreshToken_delegatesToDomainService() {
        UUID id = UUID.randomUUID();
        accountService.updateRefreshToken(id, "token");
        verify(accountDomainService).updateRefreshToken(id, "token");
    }
}
