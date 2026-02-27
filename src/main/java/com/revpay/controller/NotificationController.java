package com.revpay.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revpay.model.Notification;
import com.revpay.service.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * GET USER NOTIFICATIONS
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsForUser(
            @PathVariable Long userId) {

        List<Notification> notifications =
                notificationService.getNotificationsForUser(userId);

        return ResponseEntity.ok(notifications);
    }

    /**
     * MARK NOTIFICATION AS READ
     */
    @PostMapping("/mark-read/{notificationId}")
    public ResponseEntity<String> markAsRead(
            @PathVariable Long notificationId) {

        notificationService.markAsRead(notificationId);

        return ResponseEntity.ok("Notification marked as read");
    }
}