package com.revpay.controller;

import com.revpay.dto.ApiResponse;
import com.revpay.service.WalletService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletControllerTest {

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    private BigDecimal balance;

    @BeforeEach
    void setUp() {
        balance = new BigDecimal("1000.00");
    }

    /**
     * Test Get Wallet Balance
     */
    @Test
    void testGetBalance() {

        when(walletService.getBalance(1L)).thenReturn(balance);

        ResponseEntity<ApiResponse<BigDecimal>> response =
                walletController.getBalance(1L);

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Wallet balance fetched successfully",
                response.getBody().getMessage());
        assertEquals(balance, response.getBody().getData());

        verify(walletService, times(1)).getBalance(1L);
    }

    /**
     * Test Add Money (Deposit)
     */
    @Test
    void testAddMoney() {

        doNothing().when(walletService)
                .addMoney(1L, new BigDecimal("500.00"), "1234");

        ResponseEntity<ApiResponse<Void>> response =
                walletController.addMoney(
                        1L,
                        new BigDecimal("500.00"),
                        "1234"
                );

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Money added to wallet successfully",
                response.getBody().getMessage());

        verify(walletService, times(1))
                .addMoney(1L, new BigDecimal("500.00"), "1234");
    }

    /**
     * Test Withdraw Money
     */
    @Test
    void testWithdrawMoney() {

        doNothing().when(walletService)
                .withdrawMoney(1L, new BigDecimal("200.00"), "1234");

        ResponseEntity<ApiResponse<Void>> response =
                walletController.withdrawMoney(
                        1L,
                        new BigDecimal("200.00"),
                        "1234"
                );

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Money withdrawn from wallet successfully",
                response.getBody().getMessage());

        verify(walletService, times(1))
                .withdrawMoney(1L, new BigDecimal("200.00"), "1234");
    }
}