package com.readingshare.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readingshare.chat.dto.MessageRequest;
import com.readingshare.chat.dto.MessageResponse;
import com.readingshare.chat.service.ChatService;
import com.readingshare.common.service.WebSocketService;
import com.readingshare.room.dto.MemberResponse;
import com.readingshare.room.dto.RoomResponse;
import com.readingshare.room.service.RoomService;

public class MessageControllerTest extends ControllerIntegrationTestBase {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChatService chatService;

    @MockitoBean
    private WebSocketService webSocketService;

    @MockitoBean
    private RoomService roomService;

    private MessageResponse createdMessage;
    private MemberResponse member;

    @BeforeEach
    void setUp() {
        member = new MemberResponse(1L, "John", 1L);
        createdMessage = new MessageResponse(
                1L,
                member.id(),
                1L,
                "Hello",
                OffsetDateTime.of(2025, 5, 20, 10, 0, 0, 0, ZoneOffset.UTC),
                "CHAT");
    }

    @Test
    void getMessagesReturnsList() throws Exception {
        when(roomService.getRoomById(1L)).thenReturn(Optional.of(new RoomResponse(1L, "general", "")));
        when(chatService.getMessagesBeforeAsDto(1L, null, 20)).thenReturn(List.of(createdMessage));

        mockMvc.perform(get("/api/rooms/{roomId}/messages", 1L)
                .with(jwt().jwt(builder -> builder
                        .subject(UUID.randomUUID().toString())
                        .claim("roles", List.of("ROLE_USER"))
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(3600)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].content", is("Hello")));

        verify(roomService, times(1)).getRoomById(1L);
        verify(chatService, times(1)).getMessagesBeforeAsDto(1L, null, 20);
    }

    @Test
    void sendMessageCreatesMessage() throws Exception {
        UUID accountId = UUID.randomUUID();
        when(roomService.getMemberByAccountAndRoom(accountId, 1L)).thenReturn(Optional.of(member));
        when(chatService.createMessageInRoom("Hello", 1L, member.id())).thenReturn(createdMessage);

        mockMvc.perform(post("/api/rooms/{roomId}/messages", 1L)
                .with(jwt().jwt(builder -> builder
                        .subject(accountId.toString())
                        .claim("roles", List.of("ROLE_USER"))
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(3600))))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MessageRequest("Hello"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.content", is("Hello")));

        verify(chatService, times(1)).createMessageInRoom("Hello", 1L, member.id());
        verify(webSocketService, times(1)).broadcastMessage();
        verify(webSocketService, times(1)).broadcastMessageToRoom(1L);
    }
}
