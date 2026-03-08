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
import com.revpay.repository.MoneyRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final AuthService authService;
    private final MoneyRequestRepository moneyRequestRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  UserRepository userRepository,
                                  WalletRepository walletRepository,
                                  AuthService authService, MoneyRequestRepository moneyRequestRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.authService = authService;
        this.moneyRequestRepository = moneyRequestRepository;
    }

    @Override
    @Transactional
    public void sendMoney(Long senderId,
                          Long receiverId,
                          BigDecimal amount,
                          String transactionPin,
                          String remarks) {

        // Validate sender != receiver
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Sender and receiver cannot be the same");
        }

        // Validate amount
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        // Fetch sender & receiver
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        // Verify sender transaction PIN
        boolean pinValid = authService.verifyTransactionPin(sender, transactionPin);
        if (!pinValid) {
            throw new IllegalArgumentException("Invalid transaction PIN");
        }

        // Fetch wallets
        Wallet senderWallet = walletRepository.findByUser(sender)
                .orElseThrow(() -> new IllegalStateException("Sender wallet not found"));

        Wallet receiverWallet = walletRepository.findByUser(receiver)
                .orElseThrow(() -> new IllegalStateException("Receiver wallet not found"));

        // Check sufficient balance
        if (senderWallet.getBalance().compareTo(amount) < 0) {
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
    }

    // Fetches all the user transaction
    @Override
    public List<Transaction> getTransactionsForUser(Long userId) {

        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Fetch sent transactions
        List<Transaction> sentTransactions =
                transactionRepository.findBySender(user);

        // Fetch received transactions
        List<Transaction> receivedTransactions =
                transactionRepository.findByReceiver(user);

        // Merge both lists
        List<Transaction> allTransactions = new ArrayList<>();
        allTransactions.addAll(sentTransactions);
        allTransactions.addAll(receivedTransactions);

        // Sort by transaction date (latest first)
        allTransactions.sort(
                (t1, t2) -> t2.getTxnDate().compareTo(t1.getTxnDate())
        );

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

        return csv.toString().getBytes();
    }

    @Override
    public TransactionSummaryResponse getTransactionSummary(Long userId) {

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

        return transactionRepository.getTopCustomers(businessId);
    }

    @Override
    public RevenueReportResponse getRevenueReport(Long businessId) {

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