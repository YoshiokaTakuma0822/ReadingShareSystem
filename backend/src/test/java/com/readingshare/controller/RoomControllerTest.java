package com.readingshare.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.readingshare.room.dto.MemberResponse;
import com.readingshare.room.dto.RoomResponse;
import com.readingshare.room.service.MemberService;
import com.readingshare.room.service.RoomService;

public class RoomControllerTest extends ControllerIntegrationTestBase {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoomService roomService;

    @MockitoBean
    private MemberService memberService;

    @Test
    void getRoomsReturnsList() throws Exception {
        when(roomService.getAllRooms()).thenReturn(List.of(new RoomResponse(1L, "general", "")));

        mockMvc.perform(get("/api/rooms")
                .with(jwt().jwt(builder -> builder
                        .subject(UUID.randomUUID().toString())
                        .claim("roles", List.of("ROLE_USER"))
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(3600)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));

        verify(roomService, times(1)).getAllRooms();
    }

    @Test
    void getRoomMembersReturnsList() throws Exception {
        UUID accountId = UUID.randomUUID();
        when(roomService.getMemberByAccountAndRoom(accountId, 1L))
                .thenReturn(Optional.of(new MemberResponse(99L, "tester", 1L)));
        when(roomService.getRoomById(1L)).thenReturn(Optional.of(new RoomResponse(1L, "general", "")));
        when(memberService.getAllMembersByRoomId(1L))
                .thenReturn(List.of(new MemberResponse(1L, "John", 1L), new MemberResponse(2L, "Jane", 1L)));

        mockMvc.perform(get("/api/rooms/{id}/members", 1L)
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
}
