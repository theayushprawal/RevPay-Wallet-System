package com.revpay.controller;

import com.revpay.dto.ApiResponse;
import com.revpay.dto.AuthResponse;
import com.revpay.model.SecurityQuestion;
import com.revpay.repository.SecurityQuestionRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "User registered successfully", null));
    }

    /**
     * LOGIN API
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @RequestParam("loginId") String loginId,
            @RequestParam("password") String password) {

        AuthResponse authResponse = authService.login(loginId, password);
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", authResponse));
    }

    /**
     * FORGOT PASSWORD - FETCH SECURITY QUESTION
     */
    @GetMapping("/forgot-password/question")
    public ResponseEntity<ApiResponse<String>> getSecurityQuestion(@RequestParam String loginId) {
        User user = userRepository.findByEmailOrPhone(loginId, loginId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        SecurityQuestion securityQuestion = securityQuestionRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Security question not set"));

        return ResponseEntity.ok(new ApiResponse<>(true, "Security question fetched successfully", securityQuestion.getQuestion()));
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

        User user = userRepository.findByEmailOrPhone(loginId, loginId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return ResponseEntity.ok(new ApiResponse<>(true, "Security question verified", user.getUserId()));
    }

    /**
     * FORGOT PASSWORD - RESET
     * Note: In a production banking app, this would require a time-sensitive,
     * encrypted reset token rather than just a userId, but this is acceptable for this scope.
     * I'll do this later, maybe/maybe not!
     */
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestParam Long userId,
            @RequestParam String newPassword) {

        authService.resetPassword(userId, newPassword);
        return ResponseEntity.ok(new ApiResponse<>(true, "Password reset successful", null));
    }

    /**
     * CHANGE PASSWORD (Logged in user)
     */
    @PreAuthorize("hasAnyAuthority('PERSONAL', 'BUSINESS')")
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestParam Long userId,
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {

        verifyUserIdentity(userId); // IDOR (Insecure Direct Object Reference) PROTECTION
        authService.changePassword(userId, currentPassword, newPassword);
        return ResponseEntity.ok(new ApiResponse<>(true, "Password changed successfully", null));
    }

    /**
     * CHANGE TRANSACTION PIN (Logged in user)
     */
    @PreAuthorize("hasAnyAuthority('PERSONAL', 'BUSINESS')")
    @PostMapping("/change-pin")
    public ResponseEntity<ApiResponse<Void>> changeTransactionPin(
            @RequestParam Long userId,
            @RequestParam String oldPin,
            @RequestParam String newPin) {

        verifyUserIdentity(userId); // IDOR (Insecure Direct Object Reference) PROTECTION
        authService.changeTransactionPin(userId, oldPin, newPin);
        return ResponseEntity.ok(new ApiResponse<>(true, "Transaction PIN changed successfully", null));
    }

    // ==========================================
    // SECURITY UTILITY METHOD
    // ==========================================
    private void verifyUserIdentity(Long requestedUserId) {
        // 1. Get the loginId (email/phone) from the JWT context
        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Fetch the actual user from the DB
        User loggedInUser = userRepository.findByEmailOrPhone(loggedInUsername, loggedInUsername)
                .orElseThrow(() -> new SecurityException("Unauthorized: Invalid token identity"));

        // 3. Compare the IDs
        if (!loggedInUser.getUserId().equals(requestedUserId)) {
            throw new SecurityException("Unauthorized: You do not have permission to modify this account.");
        }
    }
}