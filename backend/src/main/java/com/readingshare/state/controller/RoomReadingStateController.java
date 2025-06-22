package com.readingshare.state.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readingshare.state.domain.model.RoomReadingState;
import com.readingshare.state.domain.model.UserReadingState;
import com.readingshare.state.dto.RoomReadingStateResponse;
import com.readingshare.state.dto.UpdateUserReadingStateRequest;
import com.readingshare.state.dto.UserReadingStateResponse;
import com.readingshare.state.service.RoomReadingStateService;

/**
 * RoomReadingStateControllerは、部屋の読書状態に関連するAPIエンドポイントを処理します。
 * このクラスは、読書状態の取得と更新を行うメソッドを提供します。
 *
 * @author 02003
 * @componentId C4
 * @moduleName 部屋読書状態コントローラー
 * @see RoomReadingStateService
 * @see RoomReadingStateResponse
 * @see UpdateUserReadingStateRequest
 * @see UserReadingStateResponse
 */
@RestController
@RequestMapping("/api/rooms/{roomId}/states")
public class RoomReadingStateController {
    private final RoomReadingStateService service;

    public RoomReadingStateController(RoomReadingStateService service) {
        this.service = service;
    }

    /**
     * ユーザーの読書状態を更新します。
     *
     * @param roomId  部屋のID
     * @param request 更新リクエスト
     * @return レスポンスエンティティ
     */
    @PostMapping("/{memberId}")
    public ResponseEntity<Void> updateUserReadingState(
            @PathVariable String roomId,
            @RequestBody UpdateUserReadingStateRequest request) {
        UserReadingState userState = new UserReadingState(
                request.userId(),
                request.currentPage(),
                request.comment());
        service.updateUserReadingState(roomId, userState);
        return ResponseEntity.ok().build();
    }

    /**
     * 部屋の読書状態を取得します。
     *
     * @param roomId 部屋のID
     * @return レスポンスエンティティ（部屋の読書状態）
     */
    @GetMapping("/{memberId}")
    public ResponseEntity<RoomReadingStateResponse> getRoomReadingState(@PathVariable String roomId) {
        RoomReadingState state = service.getRoomReadingState(roomId);
        if (state == null) {
            return ResponseEntity.notFound().build();
        }

        RoomReadingStateResponse response = new RoomReadingStateResponse(
                state.getRoomId(),
                state.getAllUserStates().stream()
                        .map(userState -> new UserReadingStateResponse(
                                userState.getUserId(),
                                userState.getCurrentPage(),
                                userState.getComment()))
                        .toList());

        return ResponseEntity.ok(response);
    }
}
