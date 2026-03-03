package com.revpay.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public AuthController(AuthService authService,
                          UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    /**
     * REGISTER API
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {

        // Basic null checks
        if (request == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }

        // Mandatory fields
        if (request.getFullName() == null || request.getEmail() == null
                || request.getPhone() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("Missing required registration fields");
        }

        // REQUIRED because of ONE security question rule
        if (request.getSecurityQuestion() == null) {
            throw new IllegalArgumentException("Security question is required");
        }

        if (request.getSecurityQuestion().getQuestion() == null
                || request.getSecurityQuestion().getAnswer() == null) {
            throw new IllegalArgumentException("Security question and answer are required");
        }

        // ===== REQUIRED Transaction Pin VALIDATION =====

        if (request.getTransactionPin() == null || request.getTransactionPin().isBlank()) {
            throw new IllegalArgumentException("Transaction PIN is required");
        }

        if (!request.getTransactionPin().matches("\\d{4}")) {
            throw new IllegalArgumentException("Transaction PIN must be exactly 4 digits");
        }

        authService.registerUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("User registered successfully");
    }

    /**
     * LOGIN API
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestParam("loginId") String loginId,
            @RequestParam("password") String password) {

        User user = authService.login(loginId, password);

        // For now, we just return a success message.
        // Later this can return JWT / session details.
        return ResponseEntity.ok("Login successful for user: " + user.getFullName());
    }

    /**
     * FORGOT PASSWORD - VERIFY Security Question
     */
    @PostMapping("/forgot-password/verify")
    public ResponseEntity<Long> verifySecurityQuestion(
            @RequestParam String loginId,
            @RequestParam String answer) {

        boolean verified = authService.verifySecurityQuestion(loginId, answer);

        if (!verified) {
            throw new IllegalArgumentException("Security answer is incorrect");
        }

        // Fetch user to return userId for next step
        User user = userRepository
                .findByEmailOrPhone(loginId, loginId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return ResponseEntity.ok(user.getUserId());
    }

    /**
     * FORGOT PASSWORD - RESET
     */
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<String> resetPassword(
            @RequestParam Long userId,
            @RequestParam String newPassword) {

        authService.resetPassword(userId, newPassword);

        return ResponseEntity.ok("Password reset successful");
    }
}