package com.revpay.service.impl;

import com.revpay.dto.DashboardSummaryResponse;
import com.revpay.model.*;
import com.revpay.model.enums.RequestStatus;
import com.revpay.model.enums.YesNoStatus;
import com.revpay.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private MoneyRequestRepository moneyRequestRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUserId(1L);
    }

    @Test
    void testGetDashboardSummarySuccess() {

        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(5000));

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction());

        List<MoneyRequest> requests = new ArrayList<>();
        requests.add(new MoneyRequest());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findTop5BySenderOrReceiverOrderByTxnDateDesc(user, user))
                .thenReturn(transactions);
        when(moneyRequestRepository.findByReceiverAndStatus(user, RequestStatus.PENDING))
                .thenReturn(requests);
        when(notificationRepository.countByUserAndIsRead(user, YesNoStatus.NO))
                .thenReturn(2L);

        DashboardSummaryResponse response = dashboardService.getDashboardSummary(1L);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(5000), response.getWalletBalance());
        assertEquals(1, response.getRecentTransactions().size());
        assertEquals(1, response.getPendingRequests().size());
        assertEquals(2L, response.getUnreadNotifications());

        verify(userRepository).findById(1L);
        verify(walletRepository).findByUser(user);
    }

    @Test
    void testUserNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> dashboardService.getDashboardSummary(1L)
        );

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testWalletNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(walletRepository.findByUser(user)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                IllegalStateException.class,
                () -> dashboardService.getDashboardSummary(1L)
        );

        assertEquals("Wallet not found", exception.getMessage());
    }

    @Test
    void testNoTransactionsOrRequests() {

        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.ZERO);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));
        when(transactionRepository.findTop5BySenderOrReceiverOrderByTxnDateDesc(user, user))
                .thenReturn(Collections.emptyList());
        when(moneyRequestRepository.findByReceiverAndStatus(user, RequestStatus.PENDING))
                .thenReturn(Collections.emptyList());
        when(notificationRepository.countByUserAndIsRead(user, YesNoStatus.NO))
                .thenReturn(0L);

        DashboardSummaryResponse response = dashboardService.getDashboardSummary(1L);

        assertEquals(0, response.getRecentTransactions().size());
        assertEquals(0, response.getPendingRequests().size());
        assertEquals(0L, response.getUnreadNotifications());
    }
}