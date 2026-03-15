package com.revpay.service.impl;

import com.revpay.model.Transaction;
import com.revpay.model.User;
import com.revpay.model.Wallet;
import com.revpay.model.enums.TransactionStatus;
import com.revpay.model.enums.TransactionType;
import com.revpay.model.enums.NotificationType;
import com.revpay.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.revpay.repository.UserRepository;
import com.revpay.repository.WalletRepository;
import com.revpay.service.AuthService;
import com.revpay.service.WalletService;
import com.revpay.service.NotificationService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class WalletServiceImpl implements WalletService {

    private static final Logger log = LogManager.getLogger(WalletServiceImpl.class);

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;

    public WalletServiceImpl(WalletRepository walletRepository,
                             UserRepository userRepository,
                             AuthService authService, TransactionRepository transactionRepository,
                             NotificationService notificationService) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.authService = authService;
        this.transactionRepository = transactionRepository;
        this.notificationService = notificationService;
    }

    @Override
    public BigDecimal getBalance(Long userId) {

        log.info("Fetching wallet balance for userId={}", userId);

        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Wallet balance fetch failed: user not found userId={}", userId);
                    return new IllegalArgumentException("User not found");
                });

        // Fetch wallet
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> {
                    log.warn("Wallet not found for userId={}", userId);
                    return new IllegalStateException("Wallet not found for user");
                });

        // Return balance
        log.info("Wallet balance fetched userId={} balance={}", userId, wallet.getBalance());
        return wallet.getBalance();
    }

    @Override
    @Transactional
    public void addMoney(Long userId, BigDecimal amount, String transactionPin) {

        log.info("Wallet deposit initiated userId={} amount={}", userId, amount);

        // Validate amount
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Deposit failed: invalid amount userId={} amount={}", userId, amount);
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Deposit failed: user not found userId={}", userId);
                    return new IllegalArgumentException("User not found");
                });

        // Verify transaction PIN
        boolean pinValid = authService.verifyTransactionPin(user, transactionPin);
        if (!pinValid) {
            log.warn("Deposit failed: invalid transaction PIN userId={}", userId);
            throw new IllegalArgumentException("Invalid transaction PIN");
        }

        // Fetch wallet
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> {
                    log.warn("Deposit failed: wallet not found userId={}", userId);
                    return new IllegalStateException("Wallet not found");
                });

        // Add amount safely (BigDecimal)
        BigDecimal newBalance = wallet.getBalance().add(amount);
        wallet.setBalance(newBalance);
        wallet.setLastUpdated(LocalDateTime.now());

        // Save wallet
        walletRepository.save(wallet);

        // Save the Transaction Record!
        Transaction txn = new Transaction();
        txn.setReceiver(user); // The user is receiving the money into their wallet
        txn.setAmount(amount);
        txn.setTxnType(TransactionType.DEPOSIT);
        txn.setStatus(TransactionStatus.SUCCESS);
        txn.setTxnDate(LocalDateTime.now());
        txn.setRemarks("Added funds from linked account");

        transactionRepository.save(txn);

        log.info("Deposit successful userId={} amount={} newBalance={}",
                userId, amount, newBalance);
    }

    @Override
    @Transactional
    public void withdrawMoney(Long userId, BigDecimal amount, String transactionPin) {

        log.info("Wallet withdrawal initiated userId={} amount={}", userId, amount);

        // Validate amount
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Withdrawal failed: invalid amount userId={} amount={}", userId, amount);
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Withdrawal failed: user not found userId={}", userId);
                    return new IllegalArgumentException("User not found");
                });

        // Verify transaction PIN
        boolean pinValid = authService.verifyTransactionPin(user, transactionPin);
        if (!pinValid) {
            log.warn("Withdrawal failed: invalid transaction PIN userId={}", userId);
            throw new IllegalArgumentException("Invalid transaction PIN");
        }

        // Fetch wallet
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> {
                    log.warn("Withdrawal failed: wallet not found userId={}", userId);
                    return new IllegalStateException("Wallet not found");
                });

        // Check sufficient balance
        if (wallet.getBalance().compareTo(amount) < 0) {
            log.warn("Withdrawal failed: insufficient balance userId={} balance={} requested={}",
                    userId, wallet.getBalance(), amount);
            throw new IllegalStateException("Insufficient wallet balance");
        }

        // Deduct amount safely
        BigDecimal newBalance = wallet.getBalance().subtract(amount);
        wallet.setBalance(newBalance);
        wallet.setLastUpdated(LocalDateTime.now());

        // Save wallet
        walletRepository.save(wallet);

        // Save the Transaction Record!
        Transaction txn = new Transaction();
        txn.setSender(user); // The user is sending the money out to their bank
        txn.setAmount(amount);
        txn.setTxnType(TransactionType.WITHDRAW);
        txn.setStatus(TransactionStatus.SUCCESS);
        txn.setTxnDate(LocalDateTime.now());
        txn.setRemarks("Withdrew funds to linked bank");

        transactionRepository.save(txn);

        if (newBalance.compareTo(new BigDecimal("1000")) < 0) {
            notificationService.sendNotification(
                    userId,
                    "Low Balance Alert: Your wallet balance has dropped to ₹" + newBalance + ". Please add funds.",
                    NotificationType.LOW_BALANCE
            );
        }

        log.info("Withdrawal successful userId={} amount={} newBalance={}",
                userId, amount, newBalance);
    }
}