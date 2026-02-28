package com.revpay.service;

import java.util.List;

import com.revpay.model.Notification;
import com.revpay.model.User;
import com.revpay.model.enums.NotificationType;

public interface NotificationService {

    /**
     * Create a notification for a user
     */
    void createNotification(User user,
                            String message,
                            NotificationType type);

    /**
     * Get all notifications for a user
     */
    List<Notification> getNotificationsForUser(Long userId);

    /**
     * Mark a notification as read
     */
    void markAsRead(Long notificationId);

    /**
     * Send notification
     */
    void sendNotification(Long userId, String message, NotificationType type);
}