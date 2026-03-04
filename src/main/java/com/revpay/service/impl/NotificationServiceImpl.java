package com.revpay.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revpay.model.Notification;
import com.revpay.model.User;
import com.revpay.model.enums.NotificationType;
import com.revpay.model.enums.YesNoStatus;
import com.revpay.repository.NotificationRepository;
import com.revpay.repository.UserRepository;
import com.revpay.service.NotificationService;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void createNotification(User user,
                                   String message,
                                   NotificationType type) {

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setIsRead(YesNoStatus.NO);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getNotificationsForUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public void markAsRead(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        notification.setIsRead(YesNoStatus.YES);
        notificationRepository.save(notification);
    }

    @Override
    public void sendNotification(Long userId, String message, NotificationType type) {

        if (userId == null) {
            throw new IllegalArgumentException("UserId is required for notification");
        }

        Notification notification = new Notification();
        notification.setUser(
                userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"))
        );
        notification.setMessage(message);
        notification.setType(type);
        notification.setIsRead(YesNoStatus.NO);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    @Override
    public void markNotificationAsRead(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        notification.setIsRead(YesNoStatus.YES);

        notificationRepository.save(notification);
    }
}