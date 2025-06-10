package com.readingshare.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.readingshare.account.domain.model.Account;
import com.readingshare.chat.domain.model.Message;
import com.readingshare.chat.domain.repository.MessageRepository;
import com.readingshare.chat.service.MessageService;
import com.readingshare.room.domain.model.Member;
import com.readingshare.room.domain.model.Room;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    private Account account;
    private Room room;
    private Member member;

    @BeforeEach
    void setup() {
        account = Account.create("a", "p");
        room = Room.reconstitute(1L, "room", null, UUID.randomUUID());
        member = Member.reconstitute(2L, "name", account, room);
    }

    @Test
    void createJoinMessage_createsJoinType() {
        when(messageRepository.save(any(Message.class))).thenAnswer(inv -> inv.getArgument(0));

        Message result = messageService.createJoinMessage(member, room);

        assertThat(result.getContent()).isEqualTo("name joined");
        assertThat(result.getSender()).isEqualTo(member);
        assertThat(result.getRoom()).isEqualTo(room);
        assertThat(result.getType()).isEqualTo(Message.MessageType.JOIN);
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void createLeaveMessage_createsLeaveType() {
        when(messageRepository.save(any(Message.class))).thenAnswer(inv -> inv.getArgument(0));

        Message result = messageService.createLeaveMessage(member, room);

        assertThat(result.getContent()).isEqualTo("name left");
        assertThat(result.getType()).isEqualTo(Message.MessageType.LEAVE);
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void createChatMessage_createsChatType() {
        when(messageRepository.save(any(Message.class))).thenAnswer(inv -> inv.getArgument(0));

        Message result = messageService.createChatMessage("hello", member, room);

        assertThat(result.getContent()).isEqualTo("hello");
        assertThat(result.getType()).isEqualTo(Message.MessageType.CHAT);
        verify(messageRepository).save(any(Message.class));
    }
}
