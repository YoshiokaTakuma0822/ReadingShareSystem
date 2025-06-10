package com.readingshare.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.readingshare.account.domain.model.Account;
import com.readingshare.account.domain.repository.AccountRepository;
import com.readingshare.room.domain.model.Member;
import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.repository.MemberRepository;
import com.readingshare.room.domain.repository.RoomRepository;
import com.readingshare.room.dto.MemberResponse;
import com.readingshare.room.service.RoomService;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private RoomService roomService;

    @Test
    void createRoom_throwsWhenDuplicateName() {
        when(roomRepository.existsByName("test")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> roomService.createRoom("test", "desc", UUID.randomUUID()));
    }

    @Test
    void joinRoom_createsMember() {
        UUID accountId = UUID.randomUUID();
        Account account = Account.create("a", "p");
        Room room = Room.reconstitute(1L, "room", null, UUID.randomUUID());
        Member member = Member.create("name", account, room);
        member.setId(2L);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(memberRepository.findByAccountAndRoom(account, room)).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        MemberResponse response = roomService.joinRoom(1L, accountId, "name");

        assertThat(response.id()).isEqualTo(2L);
        assertThat(response.name()).isEqualTo("name");
        assertThat(response.roomId()).isEqualTo(1L);
        verify(memberRepository).save(any(Member.class));
    }
}
