package com.readingshare.room.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readingshare.room.dto.MemberResponse;
import com.readingshare.room.service.ActiveMembersService;
import com.readingshare.room.service.MemberService;
import com.readingshare.room.service.RoomService;
import com.readingshare.security.JwtPayload;

import jakarta.validation.constraints.NotBlank;

/**
 * REST controller for member-related endpoints.
 * Uses RoomService as the aggregate root for member operations following DDD
 * principles.
 */
@RestController
@RequestMapping("/api/rooms/{roomId}/members")
public class MemberController {
    private final RoomService roomService; // Use RoomService as aggregate root for room-centric operations
    private final MemberService memberService; // Keep for individual member lookups
    private final ActiveMembersService activeUsersService;

    public MemberController(RoomService roomService, MemberService memberService,
            ActiveMembersService activeUsersService) {
        this.roomService = roomService;
        this.memberService = memberService;
        this.activeUsersService = activeUsersService;
    }

    /**
     * Get all members for a specific room.
     *
     * @param roomId the room ID
     * @return list of all members in the room
     */
    @GetMapping
    public @NonNull ResponseEntity<List<MemberResponse>> getAllMembers(@PathVariable Long roomId,
            @AuthenticationPrincipal Jwt jwt) {
        var payload = JwtPayload.fromJwt(jwt);
        UUID accountId = UUID.fromString(payload.sub());

        // Verify the user has access to this room by checking if they are a member
        roomService.getMemberByAccountAndRoom(accountId, roomId)
                .orElseThrow(() -> new RuntimeException("Access denied: Not a member of this room"));

        List<MemberResponse> members = memberService.getAllMembersByRoomId(roomId);
        return ResponseEntity.ok(members);
    }

    /**
     * Get member by ID.
     *
     * @param roomId the room ID
     * @param id     the member ID
     * @return the member if found, otherwise 404
     */
    @GetMapping("/{id}")
    public @NonNull ResponseEntity<MemberResponse> getMemberById(@PathVariable Long roomId,
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        var payload = JwtPayload.fromJwt(jwt);
        UUID accountId = UUID.fromString(payload.sub());

        // Verify the user has access to this room by checking if they are a member
        roomService.getMemberByAccountAndRoom(accountId, roomId)
                .orElseThrow(() -> new RuntimeException("Access denied: Not a member of this room"));

        Optional<MemberResponse> memberOptional = memberService.getMemberById(id);
        if (memberOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        MemberResponse member = memberOptional.get();

        // Verify that the member belongs to the specified room
        if (!roomId.equals(member.roomId())) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(member);
    }

    public record MemberCreateMemberRequest(@NotBlank String name) {
    }

    /**
     * Create a new member for the authenticated account.
     * Uses RoomService as aggregate root following DDD principles.
     *
     * Note: attempting to create a member for an account that already exists in
     * this room will result in a 409 Conflict error.
     * You can use GET /api/rooms/{roomId}/members/me to verify current membership
     * before creating.
     *
     * @param roomId  the room ID
     * @param request the member data
     * @param jwt     the JWT token containing the authenticated account
     *                information
     * @return the created member
     */
    @PostMapping
    public @NonNull ResponseEntity<MemberResponse> createMember(
            @PathVariable Long roomId,
            @Validated @RequestBody MemberCreateMemberRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        var payload = JwtPayload.fromJwt(jwt);
        UUID accountId = UUID.fromString(payload.sub());

        // Check if member already exists for this account in this room
        Optional<MemberResponse> existingMember = roomService.getMemberByAccountAndRoom(accountId, roomId);
        if (existingMember.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        // Create member through RoomService (aggregate root)
        MemberResponse savedMember = roomService.createMemberInRoom(request.name(), accountId, roomId);
        activeUsersService.addMember(savedMember);
        return ResponseEntity.ok(savedMember);
    }

    /**
     * Get active members in a specific room.
     *
     * @param roomId the room ID
     * @return list of active members in the room
     */
    @GetMapping("/active")
    public @NonNull ResponseEntity<List<MemberResponse>> getActiveMembers(
            @PathVariable Long roomId,
            @AuthenticationPrincipal Jwt jwt) {
        var payload = JwtPayload.fromJwt(jwt);
        UUID accountId = UUID.fromString(payload.sub());

        // Verify the user has access to this room
        roomService.getMemberByAccountAndRoom(accountId, roomId)
                .orElseThrow(() -> new RuntimeException("Member not found in room"));

        // Filter active members by room ID
        List<MemberResponse> allActiveMembers = activeUsersService.getActiveUsers();
        List<MemberResponse> roomActiveMembers = allActiveMembers.stream()
                .filter(activeMember -> {
                    // Check if member is in this room by getting the full member entity
                    Optional<MemberResponse> memberDetails = memberService.getMemberById(activeMember.id());
                    return memberDetails.isPresent() &&
                            roomId.equals(memberDetails.get().roomId());
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(roomActiveMembers);
    }

    /**
     * Update member activity time to prevent timeout.
     *
     * @param roomId the room ID
     * @param id     the member ID
     * @return success response if member is active, 404 if member not found
     */
    @PostMapping("/{id}/activity")
    public @NonNull ResponseEntity<Void> updateMemberActivity(@PathVariable Long roomId,
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        var payload = JwtPayload.fromJwt(jwt);
        UUID accountId = UUID.fromString(payload.sub());

        // Verify the user has access to this room by checking if they are a member
        roomService.getMemberByAccountAndRoom(accountId, roomId)
                .orElseThrow(() -> new RuntimeException("Access denied: Not a member of this room"));

        activeUsersService.updateMemberTimeout(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Check if the authenticated user is a member of the specified room.
     *
     * @param roomId the room ID
     * @param jwt    the JWT token containing the authenticated account
     * @return the member information if found, otherwise 404
     */
    @GetMapping("/me")
    public @NonNull ResponseEntity<MemberResponse> getCurrentMembership(@PathVariable Long roomId,
            @AuthenticationPrincipal Jwt jwt) {
        var payload = JwtPayload.fromJwt(jwt);
        UUID accountId = UUID.fromString(payload.sub());

        // Check if the current user is a member of this room
        Optional<MemberResponse> memberOptional = roomService.getMemberByAccountAndRoom(accountId, roomId);

        return memberOptional
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
