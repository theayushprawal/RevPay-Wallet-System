package com.revpay.controller;

import java.util.List;

import com.revpay.dto.ApiResponse;
import com.revpay.dto.NotificationPreferenceRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.revpay.model.Notification;
import com.revpay.model.NotificationPreference;
import com.revpay.service.NotificationService;

@RestController
@RequestMapping("/notifications")
@PreAuthorize("hasAnyAuthority('PERSONAL', 'BUSINESS')") // Blanket rule: Both account types can have notifications
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * GET ALL NOTIFICATIONS FOR USER
     */
    @PreAuthorize("hasAnyAuthority('PERSONAL', 'BUSINESS') and @securityGuard.isUserMatching(authentication, #userId)")
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Notification>>> getNotificationsForUser(
            @PathVariable Long userId) {

        List<Notification> notifications = notificationService.getNotificationsForUser(userId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Notifications fetched successfully", notifications)
        );
    }

    /**
     * MARK NOTIFICATION AS READ
     */
    // Added IDOR check via isNotificationOwner
    @PreAuthorize("hasAnyAuthority('PERSONAL', 'BUSINESS') and @securityGuard.isNotificationOwner(authentication, #notificationId)")
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long notificationId) {

        notificationService.markAsRead(notificationId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Notification marked as read", null)
        );
    }

    /**
     * UPDATE NOTIFICATION PREFERENCE
     */
    // Verifies that the userId inside the JSON payload belongs to the token holder
    @PreAuthorize("hasAnyAuthority('PERSONAL', 'BUSINESS') and @securityGuard.isUserMatching(authentication, #request.userId)")
    @PostMapping("/preferences")
    public ResponseEntity<ApiResponse<Void>> updatePreference(
            @RequestBody NotificationPreferenceRequest request) {

        notificationService.updateNotificationPreference(request);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Notification preference updated", null)
        );
    }

    /**
     * GET USER NOTIFICATION PREFERENCES
     */
    @PreAuthorize("hasAnyAuthority('PERSONAL', 'BUSINESS') and @securityGuard.isUserMatching(authentication, #userId)")
    @GetMapping("/preferences/{userId}")
    public ResponseEntity<ApiResponse<List<NotificationPreference>>> getPreferences(
            @PathVariable Long userId) {

        List<NotificationPreference> preferences = notificationService.getPreferences(userId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Notification preferences fetched successfully", preferences)
        );
    }
}