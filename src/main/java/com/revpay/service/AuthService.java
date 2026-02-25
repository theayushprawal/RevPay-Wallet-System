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
     * Set or update transaction PIN.
     */
    void setTransactionPin(Long userId, String rawPin);

    /**
     * Verify transaction PIN (used by wallet/transaction services).
     */
    boolean verifyTransactionPin(User user, String rawPin);
}