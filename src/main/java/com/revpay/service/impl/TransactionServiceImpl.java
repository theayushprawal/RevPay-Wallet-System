package com.revpay.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.revpay.dto.RevenueReportResponse;
import com.revpay.dto.TopCustomerResponse;
import com.revpay.dto.TransactionFilterRequest;
import com.revpay.dto.TransactionSummaryResponse;
import com.revpay.model.User;
import com.revpay.model.Wallet;
import com.revpay.model.enums.TransactionStatus;
import com.revpay.model.enums.TransactionType;
import com.revpay.model.enums.UserType;
import com.revpay.model.enums.NotificationType;
import com.revpay.repository.MoneyRequestRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revpay.model.Transaction;
import com.revpay.repository.TransactionRepository;
import com.revpay.repository.UserRepository;
import com.revpay.repository.WalletRepository;
import com.revpay.service.AuthService;
import com.revpay.service.TransactionService;
import com.revpay.service.NotificationService;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger log = LogManager.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final AuthService authService;
    private final MoneyRequestRepository moneyRequestRepository;
    private final NotificationService notificationService;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  UserRepository userRepository,
                                  WalletRepository walletRepository,
                                  AuthService authService,
                                  MoneyRequestRepository moneyRequestRepository,
                                  NotificationService notificationService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.authService = authService;
        this.moneyRequestRepository = moneyRequestRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public void sendMoney(Long senderId,
                          Long receiverId,
                          BigDecimal amount,
                          String transactionPin,
                          String remarks) {

        log.info("Initiating money transfer: senderId={}, receiverId={}, amount={}",
                senderId, receiverId, amount);

        if (senderId.equals(receiverId)) {
            log.warn("Money transfer failed: sender and receiver are same userId={}", senderId);
            throw new IllegalArgumentException("Sender and receiver cannot be the same");
        }

        // Validate amount
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Money transfer failed: invalid amount senderId={}", senderId);
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        // Fetch sender & receiver
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> {
                    log.warn("Money transfer failed: sender not found senderId={}", senderId);
                    return new IllegalArgumentException("Sender not found");
                });

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> {
                    log.warn("Money transfer failed: receiver not found receiverId={}", receiverId);
                    return new IllegalArgumentException("Receiver not found");
                });

        // Verify sender transaction PIN
        boolean pinValid = authService.verifyTransactionPin(sender, transactionPin);
        if (!pinValid) {
            log.warn("Money transfer failed: invalid transaction PIN userId={}", senderId);
            throw new IllegalArgumentException("Invalid transaction PIN");
        }

        // Fetch wallets
        Wallet senderWallet = walletRepository.findByUser(sender)
                .orElseThrow(() -> {
                    log.error("Sender wallet not found userId={}", senderId);
                    return new IllegalStateException("Sender wallet not found");
                });

        Wallet receiverWallet = walletRepository.findByUser(receiver)
                .orElseThrow(() -> {
                    log.error("Receiver wallet not found userId={}", receiverId);
                    return new IllegalStateException("Receiver wallet not found");
                });

        // Check sufficient balance
        if (senderWallet.getBalance().compareTo(amount) < 0) {
            log.warn("Money transfer failed: insufficient balance userId={}, balance={}, amount={}",
                    senderId, senderWallet.getBalance(), amount);
            throw new IllegalStateException("Insufficient balance");
        }

        // Debit sender
        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        senderWallet.setLastUpdated(LocalDateTime.now());

        // Credit receiver
        receiverWallet.setBalance(receiverWallet.getBalance().add(amount));
        receiverWallet.setLastUpdated(LocalDateTime.now());

        // Save wallets
        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        // Create SENDER transaction record
        Transaction senderTxn = new Transaction();
        senderTxn.setSender(sender);
        senderTxn.setReceiver(receiver);
        senderTxn.setAmount(amount);
        senderTxn.setTxnType(TransactionType.SEND);
        senderTxn.setStatus(TransactionStatus.SUCCESS);
        senderTxn.setRemarks(remarks);
        senderTxn.setTxnDate(LocalDateTime.now());

        // Create RECEIVER transaction record
        Transaction receiverTxn = new Transaction();
        receiverTxn.setSender(sender);
        receiverTxn.setReceiver(receiver);
        receiverTxn.setAmount(amount);
        receiverTxn.setTxnType(TransactionType.RECEIVE);
        receiverTxn.setStatus(TransactionStatus.SUCCESS);
        receiverTxn.setRemarks(remarks);
        receiverTxn.setTxnDate(LocalDateTime.now());

        // Save transactions
        transactionRepository.save(senderTxn);
        transactionRepository.save(receiverTxn);

        BigDecimal senderNewBalance = senderWallet.getBalance();
        if (senderNewBalance.compareTo(new BigDecimal("1000")) < 0) {
            notificationService.sendNotification(
                    senderId,
                    "Low Balance Alert: Your wallet balance has dropped to ₹" + senderNewBalance + ". Please add funds.",
                    NotificationType.LOW_BALANCE
            );
        }

        log.info("Money transfer successful: senderId={}, receiverId={}, amount={}",
                senderId, receiverId, amount);
    }

    // Fetches all the user transaction
    @Override
    public List<Transaction> getTransactionsForUser(Long userId) {

        log.info("Fetching transactions for userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Transaction fetch failed: user not found userId={}", userId);
                    return new IllegalArgumentException("User not found");
                });

        // Fetch sent and received transactions
        List<Transaction> sentTransactions = transactionRepository.findBySender(user);
        List<Transaction> receivedTransactions = transactionRepository.findByReceiver(user);

        // Merge both lists
        List<Transaction> allTransactions = new ArrayList<>();
        allTransactions.addAll(sentTransactions);
        allTransactions.addAll(receivedTransactions);

        allTransactions.sort((t1, t2) -> t2.getTxnDate().compareTo(t1.getTxnDate()));

        log.info("Transactions fetched successfully userId={}, count={}", userId, allTransactions.size());

        return allTransactions;
    }

    // Helper method for above method
    private String normalizeSearchTerm(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return null;
        }
        return searchTerm.trim();
    }

    // Pagination for Filtered Records
    @Override
    public Page<Transaction> filterTransactionsPaged(
            TransactionFilterRequest request,
            int page,
            int size) {

        log.info("Filtering transactions userId={}, page={}, size={}",
                request.getUserId(), page, size);

        if (request.getUserId() == null) {
            throw new IllegalArgumentException("UserId is required for transaction filtering");
        }

        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("txnDate").descending()
        );

        return transactionRepository.filterTransactionsPaged(
                request.getUserId(),
                request.getTxnType(),
                request.getStatus(),
                request.getStartDate(),
                request.getEndDate(),
                request.getMinAmount(),
                request.getMaxAmount(),
                normalizeSearchTerm(request.getSearchTerm()),
                pageable
        );
    }

    @Override
    public byte[] exportTransactionsToCsv(Long userId) {

        log.info("Exporting transactions CSV for userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Transaction> transactions =
                transactionRepository.findBySenderOrReceiver(user, user);

        StringBuilder csv = new StringBuilder();

        // CSV header
        csv.append("TransactionId,Sender,Receiver,Amount,Type,Status,Date\n");

        for (Transaction txn : transactions) {

            csv.append(txn.getTxnId()).append(",");

            csv.append(txn.getSender() != null
                    ? txn.getSender().getFullName()
                    : "SYSTEM").append(",");

            csv.append(txn.getReceiver() != null
                    ? txn.getReceiver().getFullName()
                    : "SYSTEM").append(",");

            csv.append(txn.getAmount()).append(",");
            csv.append(txn.getTxnType()).append(",");
            csv.append(txn.getStatus()).append(",");
            csv.append(txn.getTxnDate()).append("\n");
        }

        log.info("CSV export completed userId={}, records={}", userId, transactions.size());

        return csv.toString().getBytes();
    }

    @Override
    public TransactionSummaryResponse getTransactionSummary(Long userId) {

        log.info("Fetching transaction summary for userId={}", userId);

        TransactionSummaryResponse response = new TransactionSummaryResponse();

        response.setTotalSent(
                transactionRepository.getTotalSent(userId)
        );

        response.setTotalReceived(
                transactionRepository.getTotalReceived(userId)
        );

        response.setPendingRequests(
                moneyRequestRepository.getPendingRequestAmount(userId)
        );

        return response;
    }

    @Override
    public List<TopCustomerResponse> getTopCustomers(Long businessId) {

        log.info("Fetching top customers for businessId={}", businessId);

        User business = userRepository.findById(businessId)
                .orElseThrow(() -> {
                    log.warn("Top customers fetch failed: user not found businessId={}", businessId);
                    return new IllegalArgumentException("User not found");
                });

        if (business.getUserType() != UserType.BUSINESS) {
            log.warn("Top customers fetch denied: user is not business userId={}", businessId);
            throw new IllegalStateException("Only business users can view top customers");
        }

        return transactionRepository.getTopCustomers(businessId);
    }

    @Override
    public RevenueReportResponse getRevenueReport(Long businessId) {

        log.info("Generating revenue report for businessId={}", businessId);

        User business = userRepository.findById(businessId)
                .orElseThrow(() -> {
                    log.warn("Revenue report failed: user not found businessId={}", businessId);
                    return new IllegalArgumentException("User not found");
                });

        if (business.getUserType() != UserType.BUSINESS) {
            log.warn("Revenue report denied: user is not business userId={}", businessId);
            throw new IllegalStateException("Only business users can view revenue reports");
        }

        RevenueReportResponse response = new RevenueReportResponse();

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime dailyStart = now.minusDays(1);
        LocalDateTime weeklyStart = now.minusDays(7);
        LocalDateTime monthlyStart = now.minusDays(30);

        response.setDailyRevenue(
                transactionRepository.getRevenueFromDate(businessId, dailyStart)
        );

        response.setWeeklyRevenue(
                transactionRepository.getRevenueFromDate(businessId, weeklyStart)
        );

        response.setMonthlyRevenue(
                transactionRepository.getRevenueFromDate(businessId, monthlyStart)
        );

        return response;
    }
}