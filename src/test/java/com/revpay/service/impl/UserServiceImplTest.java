package com.revpay.service.impl;

import com.revpay.dto.UpdateProfileRequest;
import com.revpay.model.BusinessProfile;
import com.revpay.model.User;
import com.revpay.model.enums.UserType;
import com.revpay.repository.BusinessProfileRepository;
import com.revpay.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BusinessProfileRepository businessProfileRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UpdateProfileRequest request;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUserId(1L);
        user.setFullName("Old Name");
        user.setEmail("old@email.com");
        user.setPhone("9999999999");
        user.setUserType(UserType.PERSONAL);

        request = new UpdateProfileRequest();
        request.setUserId(1L);
        request.setFullName("New Name");
        request.setEmail("new@email.com");
        request.setPhone("8888888888");
    }

    @Test
    void testUpdateProfileSuccessForPersonalUser() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.updateProfile(request);

        verify(userRepository).save(user);

        assertEquals("New Name", user.getFullName());
        assertEquals("new@email.com", user.getEmail());
        assertEquals("8888888888", user.getPhone());
    }

    @Test
    void testUpdateProfileUserNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateProfile(request);
        });
    }

    @Test
    void testUpdateProfileBusinessUserSuccess() {

        user.setUserType(UserType.BUSINESS);

        BusinessProfile profile = new BusinessProfile();
        profile.setBusinessName("Old Business");

        request.setBusinessName("New Business");
        request.setBusinessType("Retail");
        request.setAddress("Bangalore");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(businessProfileRepository.findByUser(user))
                .thenReturn(Optional.of(profile));

        userService.updateProfile(request);

        verify(userRepository).save(user);
        verify(businessProfileRepository).save(profile);

        assertEquals("New Business", profile.getBusinessName());
        assertEquals("Retail", profile.getBusinessType());
        assertEquals("Bangalore", profile.getAddress());
    }

    @Test
    void testUpdateProfileBusinessProfileNotFound() {

        user.setUserType(UserType.BUSINESS);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(businessProfileRepository.findByUser(user))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> {
            userService.updateProfile(request);
        });
    }
}