package com.revpay.service.impl;

import com.revpay.config.JwtUtil;
import com.revpay.dto.AuthResponse;
import com.revpay.dto.SecurityQuestionRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.revpay.model.User;
import com.revpay.repository.UserRepository;
import com.revpay.repository.WalletRepository;
import com.revpay.service.AuthService;
import com.revpay.dto.RegisterRequest;

import java.time.LocalDateTime;
import java.math.BigDecimal;

import com.revpay.model.*;
import com.revpay.model.enums.*;
import com.revpay.repository.BusinessProfileRepository;
import com.revpay.repository.SecurityQuestionRepository;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LogManager.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final BusinessProfileRepository businessProfileRepository;
    private final SecurityQuestionRepository securityQuestionRepository;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository,
                           WalletRepository walletRepository,
                           BusinessProfileRepository businessProfileRepository,
                           SecurityQuestionRepository securityQuestionRepository,
                           PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {

        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.businessProfileRepository = businessProfileRepository;
        this.securityQuestionRepository = securityQuestionRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void registerUser(RegisterRequest request) {

        log.info("Registration attempt for email={}", request.getEmail());

        if (request == null) {
            log.error("Registration request is null");
            throw new IllegalArgumentException("Registration request cannot be null");
        }

        // Check uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: email already registered {}", request.getEmail());
            throw new IllegalArgumentException("Email already registered");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            log.warn("Registration failed: phone already registered {}", request.getPhone());
            throw new IllegalArgumentException("Phone number already registered");
        }

        // Checks for transaction PIN
        if (request.getTransactionPin() == null || request.getTransactionPin().isBlank()) {
            log.warn("Registration failed: transaction PIN missing for email {}", request.getEmail());
            throw new IllegalArgumentException("Transaction PIN is required");
        }

        if (!request.getTransactionPin().matches("\\d{4}")) {
            log.warn("Registration failed: invalid transaction PIN format for email {}", request.getEmail());
            throw new IllegalArgumentException("Transaction PIN must be exactly 4 digits");
        }

        // Hash password
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        String hashedTransactionPin = passwordEncoder.encode(request.getTransactionPin());

        // Create User
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(hashedPassword);
        user.setTransactionPinHash(hashedTransactionPin);
        user.setUserType(request.getUserType());
        user.setStatus(UserStatus.ACTIVE);
        user.setIsLocked(YesNoStatus.NO);
        user.setFailedAttempts(0);
        user.setCreatedAt(LocalDateTime.now());

        // Save user FIRST (important for FK relationships)
        user = userRepository.save(user);

        log.info("User registered successfully with userId={}", user.getUserId());

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setLastUpdated(LocalDateTime.now());

        walletRepository.save(wallet);

        log.info("Wallet created for userId={}", user.getUserId());

        if (request.getUserType() == UserType.BUSINESS) {

            if (request.getVerificationDocument() == null || request.getVerificationDocument().isBlank()) {
                log.warn("Business registration missing verification document for userId={}", user.getUserId());
                throw new IllegalArgumentException("Business verification document is required");
            }

            BusinessProfile profile = new BusinessProfile();
            profile.setUser(user);
            profile.setBusinessName(request.getBusinessName());
            profile.setBusinessType(request.getBusinessType());
            profile.setPanNumber(request.getPanNumber());
            profile.setAddress(request.getAddress());
            profile.setVerificationDocument(request.getVerificationDocument());
            profile.setDocumentUploaded(YesNoStatus.YES);

            // Simulated verification
            profile.setVerified(YesNoStatus.YES);

            businessProfileRepository.save(profile);

            log.info("Business profile created for userId={}", user.getUserId());
        }

        // Save Security Questions
        SecurityQuestionRequest sq = request.getSecurityQuestion();
        if (sq != null) {
            SecurityQuestion question = new SecurityQuestion();
            question.setUser(user);
            question.setQuestion(sq.getQuestion());
            question.setAnswerHash(passwordEncoder.encode(sq.getAnswer()));

            securityQuestionRepository.save(question);

            log.info("Security question saved for userId={}", user.getUserId());
        }
    }

    @Override
    public AuthResponse login(String loginId, String rawPassword) {

        log.info("Login attempt for {}", loginId);

        User user = userRepository
                .findByEmailOrPhone(loginId, loginId)
                .orElseThrow(() -> {
                    log.warn("Login failed: user not found for {}", loginId);
                    return new IllegalArgumentException("Invalid credentials");
                });

        // Check if account is locked
        if (user.getIsLocked() == YesNoStatus.YES) {
            log.warn("Login attempt on locked account userId={}", user.getUserId());
            throw new IllegalStateException("Account is locked due to multiple failed login attempts");
        }

        // Verify password
        boolean passwordMatches =
                passwordEncoder.matches(rawPassword, user.getPasswordHash());

        if (!passwordMatches) {

            // Handle failed attempt
            int attempts = user.getFailedAttempts() == null ? 0 : user.getFailedAttempts();
            attempts++;

            user.setFailedAttempts(attempts);

            // Lock account if threshold reached
            if (attempts >= 3) {
                user.setIsLocked(YesNoStatus.YES);
                log.warn("Account locked due to multiple failed login attempts userId={}", user.getUserId());
            }

            userRepository.save(user);

            log.warn("Login failed for userId={}", user.getUserId());
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Successful login → reset attempts
        user.setFailedAttempts(0);
        user.setLastLogin(LocalDateTime.now());

        userRepository.save(user);

        log.info("Login successful for userId={}", user.getUserId());

        // Generate the JWT
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getUserId(),
                user.getUserType().name()
        );
        // Return DTo
        return new AuthResponse(
                token,
                user.getUserId(),
                user.getUserType().name()
        );
    }

    @Override
    public void changePassword(Long userId, String currentPassword, String newPassword) {

        log.info("Password change request for userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Password change failed: user not found userId={}", userId);
                    return new IllegalArgumentException("User not found");
                });

        // Verify current password
        boolean matches =
                passwordEncoder.matches(currentPassword, user.getPasswordHash());

        if (!matches) {
            log.warn("Password change failed: incorrect current password userId={}", userId);
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Hash new password
        String newHashedPassword = passwordEncoder.encode(newPassword);

        // Update password
        user.setPasswordHash(newHashedPassword);

        // Update last login time
        user.setLastLogin(LocalDateTime.now());

        // Save user
        userRepository.save(user);

        log.info("Password changed successfully for userId={}", userId);
    }

    @Override
    public void changeTransactionPin(Long userId, String oldPin, String newPin) {

        log.info("Transaction PIN change requested for userId={}", userId);

        if (oldPin == null || oldPin.isBlank()
                || newPin == null || newPin.isBlank()) {
            throw new IllegalArgumentException("Old PIN and new PIN are required");
        }

        if (!newPin.matches("\\d{4}")) {
            throw new IllegalArgumentException("New transaction PIN must be exactly 4 digits");
        }

        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Transaction PIN change failed: user not found userId={}", userId);
                    return new IllegalArgumentException("User not found");
                });

        // Verify old PIN
        boolean matches = passwordEncoder.matches(
                oldPin,
                user.getTransactionPinHash()
        );

        if (!matches) {
            log.warn("Transaction PIN change failed: incorrect old PIN userId={}", userId);
            throw new IllegalArgumentException("Old transaction PIN is incorrect");
        }

        // Hash and update new PIN
        String hashedNewPin = passwordEncoder.encode(newPin);
        user.setTransactionPinHash(hashedNewPin);

        userRepository.save(user);

        log.info("Transaction PIN changed successfully for userId={}", userId);
    }

    @Override
    public boolean verifyTransactionPin(User user, String rawPin) {

        if (user == null) {
            log.error("verifyTransactionPin called with null user");
            throw new IllegalArgumentException("User cannot be null");
        }

        // If PIN is not set yet
        if (user.getTransactionPinHash() == null) {
            log.warn("Transaction PIN not set for userId={}", user.getUserId());
            return false;
        }

        // Compare raw pin with stored hash
        return passwordEncoder.matches(rawPin, user.getTransactionPinHash());
    }

    @Override
    public boolean verifySecurityQuestion(String loginId, String answer) {

        log.info("Security question verification attempt for {}", loginId);

        User user = userRepository
                .findByEmailOrPhone(loginId, loginId)
                .orElseThrow(() -> {
                    log.warn("Security question verification failed: user not found {}", loginId);
                    return new IllegalArgumentException("User not found");
                });

        // Fetch user's security question (ONE-TO-ONE)
        SecurityQuestion securityQuestion = securityQuestionRepository
                .findByUser(user)
                .orElseThrow(() -> {
                    log.warn("Security question not set for userId={}", user.getUserId());
                    return new IllegalStateException("Security question not set");
                });

        // Compare provided answer with stored hash
        return passwordEncoder.matches(
                answer,
                securityQuestion.getAnswerHash()
        );
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {

        log.info("Password reset requested for userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Password reset failed: user not found userId={}", userId);
                    return new IllegalArgumentException("User not found");
                });

        // Hash new password
        String hashedPassword = passwordEncoder.encode(newPassword);

        // Update password
        user.setPasswordHash(hashedPassword);

        // Reset security-related fields
        user.setFailedAttempts(0);
        user.setIsLocked(YesNoStatus.NO);
        user.setLastLogin(LocalDateTime.now());

        // Save user
        userRepository.save(user);

        log.info("Password reset successful for userId={}", userId);
    }
}