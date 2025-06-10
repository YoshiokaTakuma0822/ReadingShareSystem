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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readingshare.room.controller.MemberController.MemberCreateMemberRequest;
import com.readingshare.room.dto.MemberResponse;
import com.readingshare.room.service.ActiveMembersService;
import com.readingshare.room.service.MemberService;
import com.readingshare.room.service.RoomService;

public class MemberControllerTest extends ControllerIntegrationTestBase {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RoomService roomService;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private ActiveMembersService activeMembersService;

    @Test
    void createMemberReturnsMember() throws Exception {
        UUID accountId = UUID.randomUUID();
        MemberResponse created = new MemberResponse(3L, "New User", 1L);

        when(roomService.getMemberByAccountAndRoom(accountId, 1L)).thenReturn(Optional.empty());
        when(roomService.createMemberInRoom("New User", accountId, 1L)).thenReturn(created);

        mockMvc.perform(post("/api/rooms/{roomId}/members", 1L)
                .with(jwt().jwt(builder -> builder
                        .subject(accountId.toString())
                        .claim("roles", List.of("ROLE_USER"))
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(3600))))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new MemberCreateMemberRequest("New User"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("New User")));

        verify(roomService, times(1)).createMemberInRoom("New User", accountId, 1L);
        verify(activeMembersService, times(1)).addMember(created);
    }

    @Test
    void getAllMembersReturnsList() throws Exception {
        UUID accountId = UUID.randomUUID();
        when(roomService.getMemberByAccountAndRoom(accountId, 1L))
                .thenReturn(Optional.of(new MemberResponse(5L, "Tester", 1L)));
        when(memberService.getAllMembersByRoomId(1L))
                .thenReturn(List.of(new MemberResponse(1L, "John", 1L), new MemberResponse(2L, "Jane", 1L)));

        mockMvc.perform(get("/api/rooms/{roomId}/members", 1L)
                .with(jwt().jwt(builder -> builder
                        .subject(accountId.toString())
                        .claim("roles", List.of("ROLE_USER"))
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(3600)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].name", is("Jane")));

        verify(memberService, times(1)).getAllMembersByRoomId(1L);
    }

    @Test
    void getMemberByIdReturnsMember() throws Exception {
        UUID accountId = UUID.randomUUID();
        MemberResponse member = new MemberResponse(1L, "John", 1L);
        when(roomService.getMemberByAccountAndRoom(accountId, 1L))
                .thenReturn(Optional.of(new MemberResponse(5L, "Tester", 1L)));
        when(memberService.getMemberById(1L)).thenReturn(Optional.of(member));

        mockMvc.perform(get("/api/rooms/{roomId}/members/{id}", 1L, 1L)
                .with(jwt().jwt(builder -> builder
                        .subject(accountId.toString())
                        .claim("roles", List.of("ROLE_USER"))
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(3600)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")));

        verify(memberService, times(1)).getMemberById(1L);
    }

    @Test
    void getCurrentMembershipReturnsMember() throws Exception {
        UUID accountId = UUID.randomUUID();
        MemberResponse member = new MemberResponse(1L, "John", 1L);
        when(roomService.getMemberByAccountAndRoom(accountId, 1L))
                .thenReturn(Optional.of(member));

        mockMvc.perform(get("/api/rooms/{roomId}/members/me", 1L)
                .with(jwt().jwt(builder -> builder
                        .subject(accountId.toString())
                        .claim("roles", List.of("ROLE_USER"))
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(3600)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.roomId", is(1)));

        verify(roomService, times(1)).getMemberByAccountAndRoom(accountId, 1L);
    }

    @Test
    void getCurrentMembershipReturns404WhenNotMember() throws Exception {
        UUID accountId = UUID.randomUUID();
        when(roomService.getMemberByAccountAndRoom(accountId, 1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/rooms/{roomId}/members/me", 1L)
                .with(jwt().jwt(builder -> builder
                        .subject(accountId.toString())
                        .claim("roles", List.of("ROLE_USER"))
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(3600)))))
                .andExpect(status().isNotFound());

        verify(roomService, times(1)).getMemberByAccountAndRoom(accountId, 1L);
    }
}
