package com.revpay.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revpay.model.Notification;
import com.revpay.service.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * GET ALL NOTIFICATIONS FOR USER
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsForUser(
            @PathVariable Long userId) {

        List<Notification> notifications =
                notificationService.getNotificationsForUser(userId);

        return ResponseEntity.ok(notifications);
    }

    /**
     * MARK NOTIFICATION AS READ
     */
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<String> markAsRead(
            @PathVariable Long notificationId) {

        notificationService.markAsRead(notificationId);

        return ResponseEntity.ok("Notification marked as read");
    }
}