package com.revpay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revpay.dto.RegisterRequest;
import com.revpay.dto.SecurityQuestionRequest;
import com.revpay.model.SecurityQuestion;
import com.revpay.model.User;
import com.revpay.model.enums.UserType;
import com.revpay.repository.SecurityQuestionRepository;
import com.revpay.repository.UserRepository;
import com.revpay.service.AuthService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)   // disable Spring Security during tests
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private SecurityQuestionRepository securityQuestionRepository;

    @Autowired
    private ObjectMapper objectMapper;


    // ================= REGISTER TEST =================

    @Test
    void testRegisterUser() throws Exception {

        RegisterRequest request = new RegisterRequest();

        request.setFullName("John Doe");
        request.setEmail("john@gmail.com");
        request.setPhone("9876543210");
        request.setPassword("Password@123");
        request.setUserType(UserType.PERSONAL);
        request.setTransactionPin("1234");

        SecurityQuestionRequest question = new SecurityQuestionRequest();
        question.setQuestion("What is your pet name?");
        question.setAnswer("Dog");

        request.setSecurityQuestion(question);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value("User registered successfully"));
    }


    // ================= LOGIN TEST =================

    @Test
    void testLoginSuccess() throws Exception {

        User user = new User();
        user.setFullName("John Doe");

        Mockito.when(authService.login("john@gmail.com", "1234"))
                .thenReturn(user);

        mockMvc.perform(post("/auth/login")
                        .param("loginId", "john@gmail.com")
                        .param("password", "1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Login successful"));
    }


    // ================= FETCH SECURITY QUESTION =================

    @Test
    void testGetSecurityQuestion() throws Exception {

        User user = new User();

        SecurityQuestion question = new SecurityQuestion();
        question.setQuestion("What is your pet name?");

        Mockito.when(userRepository
                        .findByEmailOrPhone("john@gmail.com", "john@gmail.com"))
                .thenReturn(Optional.of(user));

        Mockito.when(securityQuestionRepository.findByUser(user))
                .thenReturn(Optional.of(question));

        mockMvc.perform(get("/auth/forgot-password/question")
                        .param("loginId", "john@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data")
                        .value("What is your pet name?"));
    }


    // ================= VERIFY SECURITY ANSWER =================

    @Test
    void testVerifySecurityQuestion() throws Exception {

        User user = new User();
        user.setUserId(1L);

        Mockito.when(authService.verifySecurityQuestion("john@gmail.com", "dog"))
                .thenReturn(true);

        Mockito.when(userRepository
                        .findByEmailOrPhone("john@gmail.com", "john@gmail.com"))
                .thenReturn(Optional.of(user));

        mockMvc.perform(post("/auth/forgot-password/verify")
                        .param("loginId", "john@gmail.com")
                        .param("answer", "dog"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data")
                        .value(1));
    }


    // ================= RESET PASSWORD =================

    @Test
    void testResetPassword() throws Exception {

        mockMvc.perform(post("/auth/forgot-password/reset")
                        .param("userId", "1")
                        .param("newPassword", "5678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Password reset successful"));
    }

}