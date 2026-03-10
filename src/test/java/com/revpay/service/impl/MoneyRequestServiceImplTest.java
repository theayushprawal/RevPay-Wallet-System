package com.revpay.service.impl;

import com.revpay.model.MoneyRequest;
import com.revpay.model.User;
import com.revpay.model.enums.RequestStatus;
import com.revpay.repository.MoneyRequestRepository;
import com.revpay.repository.UserRepository;
import com.revpay.service.AuthService;
import com.revpay.service.TransactionService;
import com.revpay.service.NotificationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MoneyRequestServiceImplTest {

    @Mock
    private MoneyRequestRepository moneyRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private AuthService authService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private MoneyRequestServiceImpl moneyRequestService;

    private User sender;
    private User receiver;
    private MoneyRequest request;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        sender = new User();
        sender.setUserId(1L);
        sender.setFullName("Sender User");

        receiver = new User();
        receiver.setUserId(2L);
        receiver.setFullName("Receiver User");

        request = new MoneyRequest();
        request.setRequestId(10L);
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setAmount(new BigDecimal("500"));
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        request.setExpiryDate(LocalDateTime.now().plusDays(1));
    }

    // -------------------------
    // CREATE REQUEST SUCCESS
    // -------------------------

    @Test
    void testCreateRequestSuccess() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        moneyRequestService.createRequest(
                1L,
                2L,
                new BigDecimal("500"),
                "Lunch payment"
        );

        verify(moneyRequestRepository, times(1)).save(any(MoneyRequest.class));
        verify(notificationService, times(1)).createNotification(
                eq(receiver),
                anyString(),
                any()
        );
    }

    // -------------------------
    // CREATE REQUEST SAME USER
    // -------------------------

    @Test
    void testCreateRequestSameSenderReceiver() {

        assertThrows(IllegalArgumentException.class, () ->
                moneyRequestService.createRequest(
                        1L,
                        1L,
                        new BigDecimal("200"),
                        "Invalid request"
                ));
    }

    // -------------------------
    // ACCEPT REQUEST SUCCESS
    // -------------------------

    @Test
    void testAcceptRequestSuccess() {

        when(moneyRequestRepository.findById(10L)).thenReturn(Optional.of(request));
        when(authService.verifyTransactionPin(receiver, "1234")).thenReturn(true);

        moneyRequestService.acceptRequest(10L, "1234");

        verify(transactionService, times(1)).sendMoney(
                receiver.getUserId(),
                sender.getUserId(),
                request.getAmount(),
                "1234",
                "Money request accepted"
        );

        verify(moneyRequestRepository).save(request);

        assertEquals(RequestStatus.ACCEPTED, request.getStatus());
    }

    // -------------------------
    // ACCEPT REQUEST INVALID PIN
    // -------------------------

    @Test
    void testAcceptRequestInvalidPin() {

        when(moneyRequestRepository.findById(10L)).thenReturn(Optional.of(request));
        when(authService.verifyTransactionPin(receiver, "9999")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                moneyRequestService.acceptRequest(10L, "9999"));
    }

    // -------------------------
    // DECLINE REQUEST SUCCESS
    // -------------------------

    @Test
    void testDeclineRequestSuccess() {

        when(moneyRequestRepository.findById(10L)).thenReturn(Optional.of(request));

        moneyRequestService.declineRequest(10L);

        verify(moneyRequestRepository).save(request);

        assertEquals(RequestStatus.DECLINED, request.getStatus());
    }

    // -------------------------
    // CANCEL REQUEST SUCCESS
    // -------------------------

    @Test
    void testCancelRequestSuccess() {

        when(moneyRequestRepository.findById(10L)).thenReturn(Optional.of(request));

        moneyRequestService.cancelRequest(10L);

        verify(moneyRequestRepository).save(request);

        assertEquals(RequestStatus.CANCELLED, request.getStatus());
    }

    // -------------------------
    // REQUEST NOT FOUND
    // -------------------------

    @Test
    void testRequestNotFound() {

        when(moneyRequestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                moneyRequestService.acceptRequest(99L, "1234"));
    }

}
