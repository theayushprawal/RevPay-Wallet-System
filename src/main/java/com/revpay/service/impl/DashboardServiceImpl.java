package com.revpay.service.impl;

import java.util.List;

import com.revpay.model.enums.RequestStatus;
import com.revpay.model.enums.YesNoStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.revpay.dto.DashboardSummaryResponse;
import com.revpay.model.MoneyRequest;
import com.revpay.model.Transaction;
import com.revpay.model.User;
import com.revpay.repository.MoneyRequestRepository;
import com.revpay.repository.NotificationRepository;
import com.revpay.repository.TransactionRepository;
import com.revpay.repository.UserRepository;
import com.revpay.repository.WalletRepository;
import com.revpay.service.DashboardService;

@Service
public class DashboardServiceImpl implements DashboardService {

    private static final Logger log = LogManager.getLogger(DashboardServiceImpl.class);

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final MoneyRequestRepository moneyRequestRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public DashboardServiceImpl(
            WalletRepository walletRepository,
            TransactionRepository transactionRepository,
            MoneyRequestRepository moneyRequestRepository,
            NotificationRepository notificationRepository,
            UserRepository userRepository) {

        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.moneyRequestRepository = moneyRequestRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public DashboardSummaryResponse getDashboardSummary(Long userId) {

        log.info("Fetching dashboard summary for userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Dashboard request failed: user not found userId={}", userId);
                    return new IllegalArgumentException("User not found");
                });

        DashboardSummaryResponse response = new DashboardSummaryResponse();

        // Wallet balance
        response.setWalletBalance(
                walletRepository.findByUser(user)
                        .orElseThrow(() -> {
                            log.error("Wallet not found for userId={}", userId);
                            return new IllegalStateException("Wallet not found");
                        })
                        .getBalance()
        );

        // Recent transactions
        List<Transaction> recent = transactionRepository
                .findTop5BySenderOrReceiverOrderByTxnDateDesc(user, user);

        response.setRecentTransactions(recent);

        // Pending requests
        List<MoneyRequest> pending = moneyRequestRepository
                .findByReceiverAndStatus(user, RequestStatus.PENDING);

        response.setPendingRequests(pending);

        // Unread notifications
        Long unread = notificationRepository.countByUserAndIsRead(user, YesNoStatus.NO);

        response.setUnreadNotifications(unread);

        log.info("Dashboard summary generated for userId={}, recentTxns={}, pendingRequests={}, unreadNotifications={}",
                userId,
                recent.size(),
                pending.size(),
                unread);

        return response;
    }
}