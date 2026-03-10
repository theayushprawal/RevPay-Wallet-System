package com.revpay.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import com.revpay.service.NotificationService;
import com.revpay.model.enums.NotificationType;

@Service
@Transactional
public class MoneyRequestServiceImpl implements MoneyRequestService {

    private static final Logger log = LogManager.getLogger(MoneyRequestServiceImpl.class);

    private final MoneyRequestRepository moneyRequestRepository;
    private final UserRepository userRepository;
    private final TransactionService transactionService;
    private final AuthService authService;
    private final NotificationService notificationService;

    public MoneyRequestServiceImpl(MoneyRequestRepository moneyRequestRepository,
                                   UserRepository userRepository,
                                   TransactionService transactionService,
                                   AuthService authService,
                                   NotificationService notificationService) {
        this.moneyRequestRepository = moneyRequestRepository;
        this.userRepository = userRepository;
        this.transactionService = transactionService;
        this.authService = authService;
        this.notificationService = notificationService;
    }

    /**
     * CREATE REQUEST
     */
    @Override
    public void createRequest(Long senderId,
                              Long receiverId,
                              BigDecimal amount,
                              String note) {

        log.info("Creating money request senderId={} receiverId={} amount={}",
                senderId, receiverId, amount);

        if (senderId.equals(receiverId)) {
            log.warn("Money request failed: sender and receiver same userId={}", senderId);
            throw new IllegalArgumentException("Sender and receiver cannot be same");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Money request failed: invalid amount senderId={}", senderId);
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> {
                    log.warn("Money request failed: sender not found senderId={}", senderId);
                    return new IllegalArgumentException("Sender not found");
                });

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> {
                    log.warn("Money request failed: receiver not found receiverId={}", receiverId);
                    return new IllegalArgumentException("Receiver not found");
                });

        MoneyRequest request = new MoneyRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setAmount(amount);
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        request.setExpiryDate(LocalDateTime.now().plusDays(1));
        request.setRemarks(note);

        moneyRequestRepository.save(request);

        log.info("Money request created requestId={} senderId={} receiverId={}",
                request.getRequestId(), senderId, receiverId);

        notificationService.createNotification(
                receiver,
                "You received a money request of ₹" + amount + " from " + sender.getFullName(),
                NotificationType.MONEY_REQUEST
        );
    }

    /**
     * ACCEPT REQUEST
     */
    @Override
    public void acceptRequest(Long requestId,
                              String transactionPin) {

        log.info("Accepting money request requestId={}", requestId);

        MoneyRequest request = moneyRequestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.warn("Accept request failed: request not found requestId={}", requestId);
                    return new IllegalArgumentException("Request not found");
                });

        if (request.getStatus() != RequestStatus.PENDING) {
            log.warn("Accept request failed: request not pending requestId={}", requestId);
            throw new IllegalStateException("Request is not pending");
        }

        if (request.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("Accept request failed: request expired requestId={}", requestId);
            throw new IllegalStateException("Money request has expired");
        }

        User receiver = request.getReceiver();

        // Verify transaction PIN of receiver (who pays)
        boolean pinValid = authService.verifyTransactionPin(receiver, transactionPin);
        if (!pinValid) {
            log.warn("Accept request failed: invalid PIN userId={}", receiver.getUserId());
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

        log.info("Money request accepted requestId={} amount={}",
                requestId, request.getAmount());

        notificationService.createNotification(
                request.getSender(),
                "Your money request of ₹" + request.getAmount() + " was accepted by "
                        + receiver.getFullName(),
                NotificationType.MONEY_REQUEST
        );
    }

    /**
     * DECLINE REQUEST
     */
    @Override
    public void declineRequest(Long requestId) {

        log.info("Declining money request requestId={}", requestId);

        MoneyRequest request = moneyRequestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.warn("Decline request failed: request not found requestId={}", requestId);
                    return new IllegalArgumentException("Request not found");
                });

        if (request.getStatus() != RequestStatus.PENDING) {
            log.warn("Decline request failed: request not pending requestId={}", requestId);
            throw new IllegalStateException("Request is not pending");
        }

        if (request.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("Decline request failed: request expired requestId={}", requestId);
            throw new IllegalStateException("Money request has expired");
        }

        request.setStatus(RequestStatus.DECLINED);

        moneyRequestRepository.save(request);

        log.info("Money request declined requestId={}", requestId);

        notificationService.createNotification(
                request.getSender(),
                "Your money request of ₹" + request.getAmount() + " was declined by "
                        + request.getReceiver().getFullName(),
                NotificationType.MONEY_REQUEST
        );
    }

    /**
     * CANCEL REQUEST
     */
    @Override
    public void cancelRequest(Long requestId) {

        log.info("Cancelling money request requestId={}", requestId);

        MoneyRequest request = moneyRequestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.warn("Cancel request failed: request not found requestId={}", requestId);
                    return new IllegalArgumentException("Request not found");
                });

        if (request.getStatus() != RequestStatus.PENDING) {
            log.warn("Cancel request failed: request not pending requestId={}", requestId);
            throw new IllegalStateException("Only pending requests can be cancelled");
        }

        if (request.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("Cancel request failed: request expired requestId={}", requestId);
            throw new IllegalStateException("Money request has expired");
        }

        request.setStatus(RequestStatus.CANCELLED);
        moneyRequestRepository.save(request);

        log.info("Money request cancelled requestId={}", requestId);

        notificationService.createNotification(
                request.getReceiver(),
                "Money request of ₹" + request.getAmount() + " was cancelled by "
                        + request.getSender().getFullName(),
                NotificationType.MONEY_REQUEST
        );
    }

    @Override
    public List<MoneyRequest> getIncomingRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return moneyRequestRepository.findByReceiver(user);
    }

    @Override
    public List<MoneyRequest> getOutgoingRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return moneyRequestRepository.findBySender(user);
    }
}