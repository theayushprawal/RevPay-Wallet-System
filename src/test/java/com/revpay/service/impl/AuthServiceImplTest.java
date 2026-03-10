package com.revpay.service.impl;

import com.revpay.dto.RegisterRequest;
import com.revpay.dto.SecurityQuestionRequest;
import com.revpay.model.*;
import com.revpay.model.enums.UserStatus;
import com.revpay.model.enums.UserType;
import com.revpay.model.enums.YesNoStatus;
import com.revpay.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private BusinessProfileRepository businessProfileRepository;

    @Mock
    private SecurityQuestionRepository securityQuestionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private User user;

    @BeforeEach
    void setUp() {

        registerRequest = new RegisterRequest();
        registerRequest.setFullName("Test User");
        registerRequest.setEmail("test@mail.com");
        registerRequest.setPhone("9999999999");
        registerRequest.setPassword("password");
        registerRequest.setTransactionPin("1234");
        registerRequest.setUserType(UserType.PERSONAL);

        SecurityQuestionRequest sq = new SecurityQuestionRequest();
        sq.setQuestion("Pet?");
        sq.setAnswer("Dog");

        registerRequest.setSecurityQuestion(sq);

        user = new User();
        user.setUserId(1L);
        user.setEmail("test@mail.com");
        user.setPasswordHash("encoded");
        user.setTransactionPinHash("encodedPin");
        user.setIsLocked(YesNoStatus.NO);
        user.setFailedAttempts(0);
    }

    @Test
    void testRegisterUserSuccess() {

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByPhone(anyString())).thenReturn(false);

        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        when(userRepository.save(any(User.class))).thenReturn(user);

        authService.registerUser(registerRequest);

        verify(userRepository, times(1)).save(any(User.class));
        verify(walletRepository, times(1)).save(any(Wallet.class));
        verify(securityQuestionRepository, times(1)).save(any(SecurityQuestion.class));
    }

    @Test
    void testRegisterUserEmailExists() {

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            authService.registerUser(registerRequest);
        });
    }

    @Test
    void testLoginSuccess() {

        when(userRepository.findByEmailOrPhone(anyString(), anyString()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(true);

        User result = authService.login("test@mail.com", "password");

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testLoginInvalidPassword() {

        when(userRepository.findByEmailOrPhone(anyString(), anyString()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            authService.login("test@mail.com", "wrong");
        });
    }

    @Test
    void testChangePasswordSuccess() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        when(passwordEncoder.encode(anyString())).thenReturn("newEncoded");

        authService.changePassword(1L, "password", "newpass");

        verify(userRepository).save(user);
    }

    @Test
    void testVerifyTransactionPinSuccess() {

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        boolean result = authService.verifyTransactionPin(user, "1234");

        assertTrue(result);
    }

    @Test
    void testVerifySecurityQuestionSuccess() {

        SecurityQuestion question = new SecurityQuestion();
        question.setAnswerHash("encoded");

        when(userRepository.findByEmailOrPhone(anyString(), anyString()))
                .thenReturn(Optional.of(user));

        when(securityQuestionRepository.findByUser(any(User.class)))
                .thenReturn(Optional.of(question));

        when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(true);

        boolean result = authService.verifySecurityQuestion("test@mail.com", "Dog");

        assertTrue(result);
    }

    @Test
    void testResetPasswordSuccess() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        authService.resetPassword(1L, "newpass");

        verify(userRepository).save(user);
    }
}