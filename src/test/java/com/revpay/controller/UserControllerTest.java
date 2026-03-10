package com.revpay.controller;

import com.revpay.dto.ApiResponse;
import com.revpay.dto.UpdateProfileRequest;
import com.revpay.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UpdateProfileRequest request;

    @BeforeEach
    void setUp() {
        request = new UpdateProfileRequest();
    }

    /**
     * Test Update Profile
     */
    @Test
    void testUpdateProfile() {

        doNothing().when(userService).updateProfile(request);

        ResponseEntity<ApiResponse<Void>> response =
                userController.updateProfile(request);

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Profile updated successfully",
                response.getBody().getMessage());
        assertNull(response.getBody().getData());

        verify(userService, times(1)).updateProfile(request);
    }
}