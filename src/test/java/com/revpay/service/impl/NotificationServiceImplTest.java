package com.revpay.service.impl;

import com.revpay.dto.NotificationPreferenceRequest;
import com.revpay.model.Notification;
import com.revpay.model.NotificationPreference;
import com.revpay.model.User;
import com.revpay.model.enums.NotificationType;
import com.revpay.model.enums.YesNoStatus;
import com.revpay.repository.NotificationPreferenceRepository;
import com.revpay.repository.NotificationRepository;
import com.revpay.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationPreferenceRepository notificationPreferenceRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User user;
    private Notification notification;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUserId(1L);
        user.setFullName("Test User");

        notification = new Notification();
        notification.setNotificationId(10L);
        notification.setUser(user);
        notification.setMessage("Test notification");
        notification.setType(NotificationType.MONEY_REQUEST);
        notification.setIsRead(YesNoStatus.NO);
    }

    // -------------------------
    // CREATE NOTIFICATION
    // -------------------------

    @Test
    void testCreateNotificationSuccess() {

        notificationService.createNotification(
                user,
                "Test message",
                NotificationType.MONEY_REQUEST
        );

        verify(notificationRepository, times(1))
                .save(any(Notification.class));
    }

    // -------------------------
    // GET NOTIFICATIONS
    // -------------------------

    @Test
    void testGetNotificationsForUserSuccess() {

        List<Notification> list = new ArrayList<>();
        list.add(notification);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRepository.findByUserOrderByCreatedAtDesc(user))
                .thenReturn(list);

        List<Notification> result =
                notificationService.getNotificationsForUser(1L);

        assertEquals(1, result.size());
        verify(notificationRepository).findByUserOrderByCreatedAtDesc(user);
    }

    // -------------------------
    // USER NOT FOUND
    // -------------------------

    @Test
    void testGetNotificationsUserNotFound() {

        when(userRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> notificationService.getNotificationsForUser(5L));
    }

    // -------------------------
    // MARK AS READ
    // -------------------------

    @Test
    void testMarkAsReadSuccess() {

        when(notificationRepository.findById(10L))
                .thenReturn(Optional.of(notification));

        notificationService.markAsRead(10L);

        assertEquals(YesNoStatus.YES, notification.getIsRead());
        verify(notificationRepository).save(notification);
    }

    // -------------------------
    // SEND NOTIFICATION
    // -------------------------

    @Test
    void testSendNotificationSuccess() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationPreferenceRepository
                .findByUserAndType(user, NotificationType.MONEY_REQUEST))
                .thenReturn(Optional.empty());

        notificationService.sendNotification(
                1L,
                "Payment received",
                NotificationType.MONEY_REQUEST
        );

        verify(notificationRepository, times(1))
                .save(any(Notification.class));
    }

    // -------------------------
    // NOTIFICATION DISABLED
    // -------------------------

    @Test
    void testSendNotificationDisabledByPreference() {

        NotificationPreference pref = new NotificationPreference();
        pref.setEnabled(YesNoStatus.NO);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationPreferenceRepository
                .findByUserAndType(user, NotificationType.MONEY_REQUEST))
                .thenReturn(Optional.of(pref));

        notificationService.sendNotification(
                1L,
                "Payment received",
                NotificationType.MONEY_REQUEST
        );

        verify(notificationRepository, never()).save(any());
    }

    // -------------------------
    // UPDATE PREFERENCE
    // -------------------------

    @Test
    void testUpdateNotificationPreference() {

        NotificationPreferenceRequest request =
                new NotificationPreferenceRequest();

        request.setUserId(1L);
        request.setType(NotificationType.MONEY_REQUEST);
        request.setEnabled(YesNoStatus.YES);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationPreferenceRepository
                .findByUserAndType(user, NotificationType.MONEY_REQUEST))
                .thenReturn(Optional.empty());

        notificationService.updateNotificationPreference(request);

        verify(notificationPreferenceRepository)
                .save(any(NotificationPreference.class));
    }

    // -------------------------
    // GET PREFERENCES
    // -------------------------

    @Test
    void testGetPreferencesSuccess() {

        List<NotificationPreference> prefs = new ArrayList<>();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationPreferenceRepository.findByUser(user))
                .thenReturn(prefs);

        List<NotificationPreference> result =
                notificationService.getPreferences(1L);

        assertNotNull(result);
        verify(notificationPreferenceRepository).findByUser(user);
    }

}
