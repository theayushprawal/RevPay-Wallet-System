package com.revpay.service;

import java.math.BigDecimal;

public interface WalletService {

    /**
     * Get wallet balance for a user
     */
    BigDecimal getBalance(Long userId);

    /**
     * Add money to wallet (simulated deposit)
     */
    void addMoney(Long userId, BigDecimal amount, String transactionPin);

    /**
     * Withdraw money from wallet (simulated)
     */
    void withdrawMoney(Long userId, BigDecimal amount, String transactionPin);






}