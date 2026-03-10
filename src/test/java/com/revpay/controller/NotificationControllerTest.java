package com.revpay.controller;

import com.revpay.dto.ApiResponse;
import com.revpay.dto.NotificationPreferenceRequest;
import com.revpay.model.Notification;
import com.revpay.model.NotificationPreference;
import com.revpay.service.NotificationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private Notification notification;
    private NotificationPreference preference;
    private NotificationPreferenceRequest preferenceRequest;

    @BeforeEach
    void setUp() {
        notification = new Notification();
        preference = new NotificationPreference();
        preferenceRequest = new NotificationPreferenceRequest();
    }

    /**
     * Test Get Notifications For User
     */
    @Test
    void testGetNotificationsForUser() {

        when(notificationService.getNotificationsForUser(1L))
                .thenReturn(List.of(notification));

        ResponseEntity<ApiResponse<List<Notification>>> response =
                notificationController.getNotificationsForUser(1L);

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Notifications fetched successfully",
                response.getBody().getMessage());
        assertEquals(1, response.getBody().getData().size());

        verify(notificationService, times(1))
                .getNotificationsForUser(1L);
    }

    /**
     * Test Mark Notification As Read
     */
    @Test
    void testMarkAsRead() {

        doNothing().when(notificationService).markAsRead(10L);

        ResponseEntity<ApiResponse<Void>> response =
                notificationController.markAsRead(10L);

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Notification marked as read",
                response.getBody().getMessage());

        verify(notificationService, times(1))
                .markAsRead(10L);
    }

    /**
     * Test Update Notification Preference
     */
    @Test
    void testUpdatePreference() {

        doNothing().when(notificationService)
                .updateNotificationPreference(preferenceRequest);

        ResponseEntity<ApiResponse<Void>> response =
                notificationController.updatePreference(preferenceRequest);

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Notification preference updated",
                response.getBody().getMessage());

        verify(notificationService, times(1))
                .updateNotificationPreference(preferenceRequest);
    }

    /**
     * Test Get Notification Preferences
     */
    @Test
    void testGetPreferences() {

        when(notificationService.getPreferences(1L))
                .thenReturn(List.of(preference));

        ResponseEntity<ApiResponse<List<NotificationPreference>>> response =
                notificationController.getPreferences(1L);

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Notification preferences fetched successfully",
                response.getBody().getMessage());
        assertEquals(1, response.getBody().getData().size());

        verify(notificationService, times(1))
                .getPreferences(1L);
    }
}