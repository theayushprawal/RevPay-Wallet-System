package com.revpay.service.impl;

import com.revpay.model.User;
import com.revpay.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.revpay.repository.UserRepository;
import com.revpay.repository.WalletRepository;
import com.revpay.service.AuthService;
import com.revpay.service.WalletService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository,
                             UserRepository userRepository,
                             AuthService authService) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Override
    public BigDecimal getBalance(Long userId) {

        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Fetch wallet
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Wallet not found for user"));

        // Return balance
        return wallet.getBalance();
    }

    @Override
    public void addMoney(Long userId, BigDecimal amount, String transactionPin) {

        // Validate amount
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Verify transaction PIN
        boolean pinValid = authService.verifyTransactionPin(user, transactionPin);
        if (!pinValid) {
            throw new IllegalArgumentException("Invalid transaction PIN");
        }

        // Fetch wallet
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Wallet not found"));

        // Add amount safely (BigDecimal)
        BigDecimal newBalance = wallet.getBalance().add(amount);
        wallet.setBalance(newBalance);
        wallet.setLastUpdated(LocalDateTime.now());

        // Save wallet
        walletRepository.save(wallet);
    }

    @Override
    public void withdrawMoney(Long userId, BigDecimal amount, String transactionPin) {

        // Validate amount
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Verify transaction PIN
        boolean pinValid = authService.verifyTransactionPin(user, transactionPin);
        if (!pinValid) {
            throw new IllegalArgumentException("Invalid transaction PIN");
        }

        // Fetch wallet
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Wallet not found"));

        // Check sufficient balance
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient wallet balance");
        }

        // Deduct amount safely
        BigDecimal newBalance = wallet.getBalance().subtract(amount);
        wallet.setBalance(newBalance);
        wallet.setLastUpdated(LocalDateTime.now());

        // Save wallet
        walletRepository.save(wallet);
    }
}