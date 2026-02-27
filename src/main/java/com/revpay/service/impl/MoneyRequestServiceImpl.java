package com.revpay.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revpay.model.MoneyRequest;
import com.revpay.model.User;
import com.revpay.model.enums.RequestStatus;
import com.revpay.repository.MoneyRequestRepository;
import com.revpay.repository.UserRepository;
import com.revpay.service.AuthService;
import com.revpay.service.MoneyRequestService;
import com.revpay.service.TransactionService;

@Service
@Transactional
public class MoneyRequestServiceImpl implements MoneyRequestService {

    private final MoneyRequestRepository moneyRequestRepository;
    private final UserRepository userRepository;
    private final TransactionService transactionService;
    private final AuthService authService;

    @Autowired
    public MoneyRequestServiceImpl(MoneyRequestRepository moneyRequestRepository,
                                   UserRepository userRepository,
                                   TransactionService transactionService,
                                   AuthService authService) {
        this.moneyRequestRepository = moneyRequestRepository;
        this.userRepository = userRepository;
        this.transactionService = transactionService;
        this.authService = authService;
    }

    /**
     * CREATE REQUEST
     */
    @Override
    public void createRequest(Long senderId,
                              Long receiverId,
                              BigDecimal amount,
                              String note) {

        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Sender and receiver cannot be same");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        MoneyRequest request = new MoneyRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setAmount(amount);
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        request.setExpiryDate(LocalDateTime.now().plusDays(1));
        request.setRemarks(note);

        moneyRequestRepository.save(request);
    }

    /**
     * ACCEPT REQUEST
     */
    @Override
    public void acceptRequest(Long requestId,
                              String transactionPin) {

        MoneyRequest request = moneyRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Request is not pending");
        }

        if (request.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Money request has expired");
        }

        User receiver = request.getReceiver();

        // Verify transaction PIN of receiver (who pays)
        boolean pinValid = authService.verifyTransactionPin(receiver, transactionPin);
        if (!pinValid) {
            throw new IllegalArgumentException("Invalid transaction PIN");
        }

        // Perform money transfer
        transactionService.sendMoney(
                receiver.getUserId(),
                request.getSender().getUserId(),
                request.getAmount(),
                transactionPin,
                "Money request accepted"
        );

        // Update request status
        request.setStatus(RequestStatus.ACCEPTED);
        moneyRequestRepository.save(request);
    }

    /**
     * DECLINE REQUEST
     */
    @Override
    public void declineRequest(Long requestId) {

        MoneyRequest request = moneyRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Request is not pending");
        }

        if (request.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Money request has expired");
        }

        request.setStatus(RequestStatus.DECLINED);

        moneyRequestRepository.save(request);
    }

    /**
     * CANCEL REQUEST
     */
    @Override
    public void cancelRequest(Long requestId) {

        MoneyRequest request = moneyRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be cancelled");
        }

        if (request.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Money request has expired");
        }

        request.setStatus(RequestStatus.CANCELLED);
        moneyRequestRepository.save(request);
    }
}