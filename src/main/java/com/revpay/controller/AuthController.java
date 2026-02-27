package com.revpay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revpay.dto.RegisterRequest;
import com.revpay.model.User;
import com.revpay.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
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
}