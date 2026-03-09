package com.revpay.controller;

import java.util.List;

import com.revpay.dto.ApiResponse;
import com.revpay.dto.NotificationPreferenceRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revpay.model.Notification;
import com.revpay.model.NotificationPreference;
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
    public ResponseEntity<ApiResponse<List<Notification>>> getNotificationsForUser(
            @PathVariable Long userId) {

        List<Notification> notifications =
                notificationService.getNotificationsForUser(userId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Notifications fetched successfully",
                        notifications
                )
        );
    }

    /**
     * MARK NOTIFICATION AS READ
     */
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long notificationId) {

        notificationService.markAsRead(notificationId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Notification marked as read",
                        null
                )
        );
    }

    /**
     * UPDATE NOTIFICATION PREFERENCE
     */
    @PostMapping("/preferences")
    public ResponseEntity<ApiResponse<Void>> updatePreference(
            @RequestBody NotificationPreferenceRequest request) {

        notificationService.updateNotificationPreference(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Notification preference updated",
                        null
                )
        );
    }

    /**
     * GET USER NOTIFICATION PREFERENCES
     */
    @GetMapping("/preferences/{userId}")
    public ResponseEntity<ApiResponse<List<NotificationPreference>>> getPreferences(
            @PathVariable Long userId) {

        List<NotificationPreference> preferences =
                notificationService.getPreferences(userId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Notification preferences fetched successfully",
                        preferences
                )
        );
    }
}