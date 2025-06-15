package com.readingshare.chat.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.readingshare.chat.domain.model.UserProgress;
import com.readingshare.chat.dto.RecordProgressRequest;
import com.readingshare.chat.service.GetRoomProgressService;
import com.readingshare.chat.service.RecordProgressService;

/**
 * 読書進捗に関するAPIを処理するコントローラー。
 * 担当: 榎本
 */
@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final RecordProgressService recordProgressService;
    private final GetRoomProgressService getRoomProgressService;

    public ProgressController(RecordProgressService recordProgressService,
            GetRoomProgressService getRoomProgressService) {
        this.recordProgressService = recordProgressService;
        this.getRoomProgressService = getRoomProgressService;
    }

    /**
     * ユーザーの読書進捗を記録する。
     * 
     * @param roomId  進捗を記録する部屋のID
     * @param request 進捗記録リクエスト
     * @return 記録成功時はHTTP 200 OK
     */
    @PostMapping("/{roomId}/record")
    public ResponseEntity<String> recordProgress(@PathVariable Long roomId,
            @RequestBody RecordProgressRequest request) {
        // TODO: userIdは認証情報から取得する
        Long currentUserId = 1L; // 仮のユーザーID
        recordProgressService.recordProgress(roomId, currentUserId, request.getCurrentPage());
        return ResponseEntity.ok("Progress recorded successfully.");
    }

    /**
     * 特定の部屋の全ユーザーの読書進捗を取得する。
     * 
     * @param roomId 進捗を取得する部屋のID
     * @return ユーザー進捗のリスト
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<List<UserProgress>> getRoomProgress(@PathVariable Long roomId) {
        List<UserProgress> userProgressList = getRoomProgressService.getRoomProgress(roomId);
        return ResponseEntity.ok(userProgressList);
    }
}
