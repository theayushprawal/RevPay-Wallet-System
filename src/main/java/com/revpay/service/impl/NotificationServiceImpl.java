package com.revpay.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.revpay.dto.NotificationPreferenceRequest;
import com.revpay.model.NotificationPreference;
import com.revpay.repository.NotificationPreferenceRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final Logger log = LogManager.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserRepository userRepository,
                                   NotificationPreferenceRepository notificationPreferenceRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.notificationPreferenceRepository = notificationPreferenceRepository;
    }

    @Override
    public void createNotification(User user,
                                   String message,
                                   NotificationType type) {

        log.info("Creating notification for userId={} type={}",
                user != null ? user.getUserId() : null, type);

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setIsRead(YesNoStatus.NO);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);

        log.info("Notification created successfully for userId={}",
                user != null ? user.getUserId() : null);
    }

    @Override
    public List<Notification> getNotificationsForUser(Long userId) {

        log.info("Fetching notifications for userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Notification fetch failed: user not found userId={}", userId);
                    return new IllegalArgumentException("User not found");
                });

        List<Notification> notifications =
                notificationRepository.findByUserOrderByCreatedAtDesc(user);

        log.info("Notifications fetched count={} userId={}",
                notifications.size(), userId);

        return notifications;
    }

    @Override
    public void markAsRead(Long notificationId) {

        log.info("Marking notification as read notificationId={}", notificationId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> {
                    log.warn("Mark read failed: notification not found notificationId={}", notificationId);
                    return new IllegalArgumentException("Notification not found");
                });

        notification.setIsRead(YesNoStatus.YES);
        notificationRepository.save(notification);

        log.info("Notification marked as read notificationId={}", notificationId);
    }

    @Override
    public void sendNotification(Long userId, String message, NotificationType type) {

        log.info("Sending notification userId={} type={}", userId, type);

        if (userId == null) {
            log.warn("Notification send failed: userId null");
            throw new IllegalArgumentException("UserId is required for notification");
        }

        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Notification send failed: user not found userId={}", userId);
                    return new IllegalArgumentException("User not found");
                });

        // Check notification preference
        Optional<NotificationPreference> pref =
                notificationPreferenceRepository.findByUserAndType(user, type);

        if (pref.isPresent() && pref.get().getEnabled() == YesNoStatus.NO) {
            log.info("Notification skipped due to user preference userId={} type={}",
                    userId, type);
            return; // user disabled this type
        }

        // Create notification
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setIsRead(YesNoStatus.NO);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);

        log.info("Notification sent successfully userId={} type={}", userId, type);
    }

    @Override
    public void updateNotificationPreference(NotificationPreferenceRequest request) {

        log.info("Updating notification preference userId={} type={} enabled={}",
                request.getUserId(), request.getType(), request.getEnabled());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> {
                    log.warn("Update preference failed: user not found userId={}", request.getUserId());
                    return new IllegalArgumentException("User not found");
                });

        NotificationPreference pref =
                notificationPreferenceRepository
                        .findByUserAndType(user, request.getType())
                        .orElse(new NotificationPreference());

        pref.setUser(user);
        pref.setType(request.getType());
        pref.setEnabled(request.getEnabled());

        notificationPreferenceRepository.save(pref);

        log.info("Notification preference updated userId={} type={}",
                request.getUserId(), request.getType());
    }

    @Override
    public List<NotificationPreference> getPreferences(Long userId) {

        log.info("Fetching notification preferences for userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Preference fetch failed: user not found userId={}", userId);
                    return new IllegalArgumentException("User not found");
                });

        List<NotificationPreference> preferences =
                notificationPreferenceRepository.findByUser(user);

        log.info("Notification preferences fetched count={} userId={}",
                preferences.size(), userId);

        return preferences;
    }
}