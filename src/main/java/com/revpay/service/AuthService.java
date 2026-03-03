package com.revpay.service;

import com.revpay.model.User;
import com.revpay.dto.RegisterRequest;

public interface AuthService {

    /**
     * Registers a new user (PERSONAL or BUSINESS).
     * - Validates uniqueness
     * - Hashes password
     * - Creates wallet
     * - Creates business profile if needed
     */
    void registerUser(RegisterRequest request);

    /**
     * Authenticates user using email or phone + password.
     * - Handles failed attempts
     * - Locks account if required
     */
    User login(String loginId, String rawPassword);

    /**
     * Change user password after verifying current password.
     */
    void changePassword(Long userId, String currentPassword, String newPassword);

    /**
     * Update transaction PIN.
     */
    void changeTransactionPin(Long userId, String oldPin, String newPin);

    /**
     * Verify transaction PIN (used by wallet/transaction services).
     */
    boolean verifyTransactionPin(User user, String rawPin);

    /**
     * Verify Security Question to reset password
     */
    boolean verifySecurityQuestion(String loginId, String answer);

    /**
     * Resets password using forget password
     */
    void resetPassword(Long userId, String newPassword);
}