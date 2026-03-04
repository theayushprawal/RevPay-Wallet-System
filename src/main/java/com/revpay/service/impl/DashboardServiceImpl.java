package com.revpay.service.impl;

import java.util.List;

import com.revpay.model.enums.RequestStatus;
import com.revpay.model.enums.YesNoStatus;
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

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        DashboardSummaryResponse response = new DashboardSummaryResponse();

        // Wallet balance
        response.setWalletBalance(
                walletRepository.findByUser(user)
                        .orElseThrow(() -> new IllegalStateException("Wallet not found"))
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

        return response;
    }
}