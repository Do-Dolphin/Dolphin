package com.dolphin.demo.controller;

import com.dolphin.demo.dto.response.NotificationsResponse;
import com.dolphin.demo.jwt.UserDetailsImpl;
import com.dolphin.demo.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * 로그인 한 유저 sse 연결
     */
    @GetMapping(value = "/api/auth/notice/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                @RequestParam(value = "lastEventId", required = false, defaultValue = "") String lastEventId) {
        return notificationService.subscribe(userDetails.getMember().getId(), lastEventId);
    }

    /**
     * 로그인 한 유저의 모든 알림 조회
     */
    @GetMapping("/api/auth/notice/notifications")
    public ResponseEntity<NotificationsResponse> notifications(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(notificationService.findAllById(userDetails.getMember().getId()));
    }

    /**
     * 알림 읽음 상태 변경
     */
    @PatchMapping("/api/auth/notice/read")
    public ResponseEntity<Void> readNotification(@PathVariable Long id) {
        notificationService.readNotification(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
