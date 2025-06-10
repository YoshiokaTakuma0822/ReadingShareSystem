package com.readingshare.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.readingshare.account.domain.model.Account;
import com.readingshare.chat.domain.model.Message;
import com.readingshare.chat.domain.repository.MessageRepository;
import com.readingshare.chat.service.ChatService;
import com.readingshare.room.domain.model.Member;
import com.readingshare.room.domain.model.Room;
import com.readingshare.room.domain.repository.MemberRepository;
import com.readingshare.room.domain.repository.RoomRepository;
import com.readingshare.room.service.ActiveMembersService;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ActiveMembersService activeMembersService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private ChatService chatService;

    @Test
    void getMessagesBeforeAsDto_returnsDtos() {
        Account account = Account.create("a@test.com", "pass");
        Room room = Room.reconstitute(1L, "room", null, UUID.randomUUID());
        Member member = Member.reconstitute(1L, "user", account, room);
        Message message = Message.reconstitute(1L, "hi", member, room, OffsetDateTime.now(), Message.MessageType.CHAT);

        when(messageRepository.findByRoomIdOrderByCreatedAtDesc(1L, 10))
                .thenReturn(List.of(message));

        var result = chatService.getMessagesBeforeAsDto(1L, null, 10);

        assertThat(result).hasSize(1);
        var dto = result.get(0);
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.senderId()).isEqualTo(1L);
        assertThat(dto.roomId()).isEqualTo(1L);
        assertThat(dto.content()).isEqualTo("hi");
        assertThat(dto.type()).isEqualTo("CHAT");
        verify(messageRepository).findByRoomIdOrderByCreatedAtDesc(1L, 10);
    }

    @Test
    void createMessageInRoom_savesMessage() {
        Account account = Account.create("mail", "pass");
        Room room = Room.reconstitute(2L, "room", null, UUID.randomUUID());
        Member member = Member.reconstitute(3L, "name", account, room);

        when(memberRepository.findById(3L)).thenReturn(Optional.of(member));
        when(roomRepository.findById(2L)).thenReturn(Optional.of(room));
        when(messageRepository.save(any(Message.class))).thenAnswer(inv -> inv.getArgument(0));

        var dto = chatService.createMessageInRoom("hello", 2L, 3L);

        assertThat(dto.roomId()).isEqualTo(2L);
        assertThat(dto.senderId()).isEqualTo(3L);
        assertThat(dto.content()).isEqualTo("hello");
        verify(messageRepository).save(any(Message.class));
    }
}
