package com.revpay.controller;

import com.revpay.dto.ApiResponse;
import com.revpay.model.SecurityQuestion;
import com.revpay.repository.SecurityQuestionRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revpay.dto.RegisterRequest;
import com.revpay.model.User;
import com.revpay.service.AuthService;
import com.revpay.repository.UserRepository;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final SecurityQuestionRepository securityQuestionRepository;

    public AuthController(AuthService authService,
                          UserRepository userRepository,
                          SecurityQuestionRepository securityQuestionRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.securityQuestionRepository = securityQuestionRepository;
    }

    /**
     * REGISTER API
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(
            @Valid @RequestBody RegisterRequest request) {

        authService.registerUser(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "User registered successfully", null));
    }

    /**
     * LOGIN API
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(
            @RequestParam("loginId") String loginId,
            @RequestParam("password") String password) {

        User user = authService.login(loginId, password);

        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Login successful",
                        "Welcome " + user.getFullName())
        );
    }

    /**
     * FORGOT PASSWORD - FETCH SECURITY QUESTION
     */
    @GetMapping("/forgot-password/question")
    public ResponseEntity<ApiResponse<String>> getSecurityQuestion(
            @RequestParam String loginId) {

        User user = userRepository
                .findByEmailOrPhone(loginId, loginId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        SecurityQuestion securityQuestion = securityQuestionRepository
                .findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Security question not set"));

        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Security question fetched successfully",
                        securityQuestion.getQuestion())
        );
    }

    /**
     * FORGOT PASSWORD - VERIFY Security Question
     */
    @PostMapping("/forgot-password/verify")
    public ResponseEntity<ApiResponse<Long>> verifySecurityQuestion(
            @RequestParam String loginId,
            @RequestParam String answer) {

        boolean verified = authService.verifySecurityQuestion(loginId, answer);

        if (!verified) {
            throw new IllegalArgumentException("Security answer is incorrect");
        }

        User user = userRepository
                .findByEmailOrPhone(loginId, loginId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Security question verified",
                        user.getUserId())
        );
    }

    /**
     * FORGOT PASSWORD - RESET
     */
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestParam Long userId,
            @RequestParam String newPassword) {

        authService.resetPassword(userId, newPassword);

        return ResponseEntity.ok(
                new ApiResponse<>(true,
                        "Password reset successful",
                        null)
        );
    }
}