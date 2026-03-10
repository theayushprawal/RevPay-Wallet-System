package com.revpay.service.impl;

import com.revpay.model.User;
import com.revpay.model.Wallet;
import com.revpay.repository.UserRepository;
import com.revpay.repository.WalletRepository;
import com.revpay.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private WalletServiceImpl walletService;

    private User user;
    private Wallet wallet;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUserId(1L);

        wallet = new Wallet();
        wallet.setBalance(new BigDecimal("1000"));
    }

    // GET BALANCE SUCCESS
    @Test
    void testGetBalanceSuccess() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));

        BigDecimal balance = walletService.getBalance(1L);

        assertEquals(new BigDecimal("1000"), balance);
    }

    // GET BALANCE USER NOT FOUND
    @Test
    void testGetBalanceUserNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> walletService.getBalance(1L));
    }

    // ADD MONEY SUCCESS
    @Test
    void testAddMoneySuccess() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(authService.verifyTransactionPin(user, "1234")).thenReturn(true);
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));

        walletService.addMoney(1L, new BigDecimal("200"), "1234");

        verify(walletRepository).save(wallet);

        assertEquals(new BigDecimal("1200"), wallet.getBalance());
    }

    // ADD MONEY INVALID AMOUNT
    @Test
    void testAddMoneyInvalidAmount() {

        assertThrows(IllegalArgumentException.class,
                () -> walletService.addMoney(1L, new BigDecimal("0"), "1234"));
    }

    // ADD MONEY INVALID PIN
    @Test
    void testAddMoneyInvalidPin() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(authService.verifyTransactionPin(user, "1234")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> walletService.addMoney(1L, new BigDecimal("100"), "1234"));
    }

    // WITHDRAW MONEY SUCCESS
    @Test
    void testWithdrawMoneySuccess() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(authService.verifyTransactionPin(user, "1234")).thenReturn(true);
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));

        walletService.withdrawMoney(1L, new BigDecimal("200"), "1234");

        verify(walletRepository).save(wallet);

        assertEquals(new BigDecimal("800"), wallet.getBalance());
    }

    // WITHDRAW MONEY INSUFFICIENT BALANCE
    @Test
    void testWithdrawMoneyInsufficientBalance() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(authService.verifyTransactionPin(user, "1234")).thenReturn(true);
        when(walletRepository.findByUser(user)).thenReturn(Optional.of(wallet));

        assertThrows(IllegalStateException.class,
                () -> walletService.withdrawMoney(1L, new BigDecimal("2000"), "1234"));
    }

    // WITHDRAW MONEY INVALID PIN
    @Test
    void testWithdrawMoneyInvalidPin() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(authService.verifyTransactionPin(user, "1234")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> walletService.withdrawMoney(1L, new BigDecimal("100"), "1234"));
    }
}
