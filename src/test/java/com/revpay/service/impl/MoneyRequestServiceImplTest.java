package com.revpay.service.impl;

import com.revpay.model.MoneyRequest;
import com.revpay.model.User;
import com.revpay.model.enums.NotificationType;
import com.revpay.model.enums.RequestStatus;
import com.revpay.repository.MoneyRequestRepository;
import com.revpay.repository.UserRepository;
import com.revpay.service.AuthService;
import com.revpay.service.NotificationService;
import com.revpay.service.TransactionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
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
    void setup() {

        sender = new User();
        sender.setUserId(1L);
        sender.setFullName("Sender");

        receiver = new User();
        receiver.setUserId(2L);
        receiver.setFullName("Receiver");

        request = new MoneyRequest();
        request.setRequestId(10L);
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setAmount(new BigDecimal("500"));
        request.setStatus(RequestStatus.PENDING);
        request.setExpiryDate(LocalDateTime.now().plusHours(2));
    }

    // CREATE REQUEST SUCCESS
    @Test
    void testCreateRequestSuccess() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        moneyRequestService.createRequest(
                1L,
                2L,
                new BigDecimal("500"),
                "Test request"
        );

        verify(moneyRequestRepository).save(any(MoneyRequest.class));

        verify(notificationService).sendNotification(
                eq(2L),
                anyString(),
                eq(NotificationType.MONEY_REQUEST)
        );
    }

    // SAME USER ERROR
    @Test
    void testCreateRequestSameUser() {

        assertThrows(IllegalArgumentException.class, () ->
                moneyRequestService.createRequest(
                        1L,
                        1L,
                        new BigDecimal("100"),
                        "Note"
                )
        );
    }

    // INVALID AMOUNT
    @Test
    void testCreateRequestInvalidAmount() {

        assertThrows(IllegalArgumentException.class, () ->
                moneyRequestService.createRequest(
                        1L,
                        2L,
                        BigDecimal.ZERO,
                        "Note"
                )
        );
    }

    // ACCEPT REQUEST SUCCESS
    @Test
    void testAcceptRequestSuccess() {

        when(moneyRequestRepository.findById(10L)).thenReturn(Optional.of(request));
        when(authService.verifyTransactionPin(receiver, "1234")).thenReturn(true);

        moneyRequestService.acceptRequest(10L, "1234");

        verify(transactionService).sendMoney(
                eq(receiver.getUserId()),
                eq(sender.getUserId()),
                eq(request.getAmount()),
                eq("1234"),
                eq("Money request accepted")
        );

        verify(moneyRequestRepository).save(request);

        verify(notificationService).sendNotification(
                eq(sender.getUserId()),
                anyString(),
                eq(NotificationType.MONEY_REQUEST)
        );
    }

    // INVALID PIN
    @Test
    void testAcceptRequestInvalidPin() {

        when(moneyRequestRepository.findById(10L)).thenReturn(Optional.of(request));
        when(authService.verifyTransactionPin(receiver, "1234")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                moneyRequestService.acceptRequest(10L, "1234")
        );
    }

    // DECLINE REQUEST
    @Test
    void testDeclineRequest() {

        when(moneyRequestRepository.findById(10L)).thenReturn(Optional.of(request));

        moneyRequestService.declineRequest(10L);

        assertEquals(RequestStatus.DECLINED, request.getStatus());

        verify(moneyRequestRepository).save(request);

        verify(notificationService).sendNotification(
                eq(sender.getUserId()),
                anyString(),
                eq(NotificationType.MONEY_REQUEST)
        );
    }

    // CANCEL REQUEST
    @Test
    void testCancelRequest() {

        when(moneyRequestRepository.findById(10L)).thenReturn(Optional.of(request));

        moneyRequestService.cancelRequest(10L);

        assertEquals(RequestStatus.CANCELLED, request.getStatus());

        verify(moneyRequestRepository).save(request);

        verify(notificationService).sendNotification(
                eq(receiver.getUserId()),
                anyString(),
                eq(NotificationType.MONEY_REQUEST)
        );
    }

    // INCOMING REQUESTS
    @Test
    void testGetIncomingRequests() {

        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(moneyRequestRepository.findByReceiver(receiver))
                .thenReturn(List.of(request));

        List<MoneyRequest> result =
                moneyRequestService.getIncomingRequests(2L);

        assertEquals(1, result.size());
    }

    // OUTGOING REQUESTS
    @Test
    void testGetOutgoingRequests() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(moneyRequestRepository.findBySender(sender))
                .thenReturn(List.of(request));

        List<MoneyRequest> result =
                moneyRequestService.getOutgoingRequests(1L);

        assertEquals(1, result.size());
    }
}