package com.revpay.service.impl;

import com.revpay.dto.TransactionSummaryResponse;
import com.revpay.model.Transaction;
import com.revpay.model.User;
import com.revpay.model.Wallet;
import com.revpay.model.enums.UserType;
import com.revpay.repository.TransactionRepository;
import com.revpay.repository.UserRepository;
import com.revpay.repository.WalletRepository;
import com.revpay.repository.MoneyRequestRepository;
import com.revpay.service.AuthService;
import com.revpay.service.NotificationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private AuthService authService;

    @Mock
    private MoneyRequestRepository moneyRequestRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User sender;
    private User receiver;
    private Wallet senderWallet;
    private Wallet receiverWallet;

    @BeforeEach
    void setup() {

        sender = new User();
        sender.setUserId(1L);
        sender.setFullName("Sender User");

        receiver = new User();
        receiver.setUserId(2L);
        receiver.setFullName("Receiver User");

        senderWallet = new Wallet();
        senderWallet.setBalance(new BigDecimal("5000"));

        receiverWallet = new Wallet();
        receiverWallet.setBalance(new BigDecimal("1000"));
    }

    // ================= SEND MONEY SUCCESS =================

    @Test
    void testSendMoneySuccess() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        when(authService.verifyTransactionPin(sender, "1234")).thenReturn(true);

        when(walletRepository.findByUser(sender)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findByUser(receiver)).thenReturn(Optional.of(receiverWallet));

        transactionService.sendMoney(
                1L,
                2L,
                new BigDecimal("1000"),
                "1234",
                "Payment"
        );

        verify(walletRepository, times(2)).save(any(Wallet.class));
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }

    // ================= SAME USER ERROR =================

    @Test
    void testSendMoneySameUser() {

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.sendMoney(
                        1L,
                        1L,
                        new BigDecimal("100"),
                        "1234",
                        "Test"
                )
        );
    }

    // ================= INVALID AMOUNT =================

    @Test
    void testSendMoneyInvalidAmount() {

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.sendMoney(
                        1L,
                        2L,
                        BigDecimal.ZERO,
                        "1234",
                        "Test"
                )
        );
    }

    // ================= INVALID PIN =================

    @Test
    void testSendMoneyInvalidPin() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        when(authService.verifyTransactionPin(sender, "1234")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.sendMoney(
                        1L,
                        2L,
                        new BigDecimal("500"),
                        "1234",
                        "Test"
                )
        );
    }

    // ================= INSUFFICIENT BALANCE =================

    @Test
    void testSendMoneyInsufficientBalance() {

        senderWallet.setBalance(new BigDecimal("100"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        when(authService.verifyTransactionPin(sender, "1234")).thenReturn(true);

        when(walletRepository.findByUser(sender)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findByUser(receiver)).thenReturn(Optional.of(receiverWallet));

        assertThrows(IllegalStateException.class, () ->
                transactionService.sendMoney(
                        1L,
                        2L,
                        new BigDecimal("500"),
                        "1234",
                        "Test"
                )
        );
    }

    // ================= GET TRANSACTIONS =================

    @Test
    void testGetTransactionsForUser() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));

        Transaction txn = new Transaction();
        txn.setTxnDate(LocalDateTime.now());

        when(transactionRepository.findBySender(sender)).thenReturn(List.of(txn));
        when(transactionRepository.findByReceiver(sender)).thenReturn(List.of());

        List<Transaction> result =
                transactionService.getTransactionsForUser(1L);

        assertEquals(1, result.size());
    }

    // ================= EXPORT CSV =================

    @Test
    void testExportTransactionsToCsv() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));

        Transaction txn = new Transaction();
        txn.setTxnId(1L);
        txn.setSender(sender);
        txn.setReceiver(receiver);
        txn.setAmount(new BigDecimal("500"));
        txn.setTxnDate(LocalDateTime.now());

        when(transactionRepository.findBySenderOrReceiver(sender, sender))
                .thenReturn(List.of(txn));

        byte[] csv = transactionService.exportTransactionsToCsv(1L);

        assertNotNull(csv);
        assertTrue(new String(csv).contains("TransactionId"));
    }

    // ================= TRANSACTION SUMMARY =================

    @Test
    void testTransactionSummary() {

        when(transactionRepository.getTotalSent(1L))
                .thenReturn(new BigDecimal("1000"));

        when(transactionRepository.getTotalReceived(1L))
                .thenReturn(new BigDecimal("2000"));

        when(moneyRequestRepository.getPendingRequestAmount(1L))
                .thenReturn(new BigDecimal("300"));

        TransactionSummaryResponse response =
                transactionService.getTransactionSummary(1L);

        assertEquals(new BigDecimal("1000"), response.getTotalSent());
        assertEquals(new BigDecimal("2000"), response.getTotalReceived());
    }
}