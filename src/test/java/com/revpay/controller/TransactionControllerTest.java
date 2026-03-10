package com.revpay.controller;

import com.revpay.dto.*;
import com.revpay.model.Transaction;
import com.revpay.service.TransactionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private Transaction transaction;
    private TransactionFilterRequest filterRequest;
    private TransactionSummaryResponse summaryResponse;
    private RevenueReportResponse revenueReportResponse;
    private TopCustomerResponse topCustomerResponse;

    @BeforeEach
    void setUp() {
        transaction = new Transaction();
        filterRequest = new TransactionFilterRequest();
        summaryResponse = new TransactionSummaryResponse();
        revenueReportResponse = new RevenueReportResponse();
        topCustomerResponse = new TopCustomerResponse();
    }

    /**
     * Test Send Money
     */
    @Test
    void testSendMoney() {

        doNothing().when(transactionService)
                .sendMoney(1L, 2L, new BigDecimal("100.00"), "1234", "test");

        ResponseEntity<ApiResponse<Void>> response =
                transactionController.sendMoney(
                        1L,
                        2L,
                        new BigDecimal("100.00"),
                        "1234",
                        "test"
                );

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Money sent successfully",
                response.getBody().getMessage());

        verify(transactionService, times(1))
                .sendMoney(1L, 2L, new BigDecimal("100.00"), "1234", "test");
    }

    /**
     * Test Transaction History
     */
    @Test
    void testGetTransactionHistory() {

        when(transactionService.getTransactionsForUser(1L))
                .thenReturn(List.of(transaction));

        ResponseEntity<ApiResponse<List<Transaction>>> response =
                transactionController.getTransactionHistory(1L);

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Transaction history fetched successfully",
                response.getBody().getMessage());
        assertEquals(1, response.getBody().getData().size());

        verify(transactionService).getTransactionsForUser(1L);
    }

    /**
     * Test Filter Transactions Paged
     */
    @Test
    void testFilterTransactionsPaged() {

        Page<Transaction> page =
                new PageImpl<>(List.of(transaction));

        when(transactionService.filterTransactionsPaged(filterRequest, 0, 10))
                .thenReturn(page);

        ResponseEntity<ApiResponse<Page<Transaction>>> response =
                transactionController.filterTransactionsPaged(
                        filterRequest,
                        0,
                        10
                );

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Filtered transactions fetched successfully",
                response.getBody().getMessage());

        verify(transactionService)
                .filterTransactionsPaged(filterRequest, 0, 10);
    }

    /**
     * Test Export CSV
     */
    @Test
    void testExportTransactionsCsv() {

        byte[] csvData = "id,amount".getBytes();

        when(transactionService.exportTransactionsToCsv(1L))
                .thenReturn(csvData);

        ResponseEntity<byte[]> response =
                transactionController.exportTransactionsCsv(1L);

        assertNotNull(response);
        assertArrayEquals(csvData, response.getBody());
        assertEquals("text/csv",
                response.getHeaders().getFirst("Content-Type"));

        verify(transactionService).exportTransactionsToCsv(1L);
    }

    /**
     * Test Transaction Summary
     */
    @Test
    void testGetTransactionSummary() {

        when(transactionService.getTransactionSummary(1L))
                .thenReturn(summaryResponse);

        ResponseEntity<ApiResponse<TransactionSummaryResponse>> response =
                transactionController.getTransactionSummary(1L);

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals(summaryResponse, response.getBody().getData());

        verify(transactionService).getTransactionSummary(1L);
    }

    /**
     * Test Top Customers
     */
    @Test
    void testGetTopCustomers() {

        when(transactionService.getTopCustomers(1L))
                .thenReturn(List.of(topCustomerResponse));

        ResponseEntity<ApiResponse<List<TopCustomerResponse>>> response =
                transactionController.getTopCustomers(1L);

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());

        verify(transactionService).getTopCustomers(1L);
    }

    /**
     * Test Revenue Report
     */
    @Test
    void testGetRevenueReport() {

        when(transactionService.getRevenueReport(1L))
                .thenReturn(revenueReportResponse);

        ResponseEntity<ApiResponse<RevenueReportResponse>> response =
                transactionController.getRevenueReport(1L);

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals(revenueReportResponse, response.getBody().getData());

        verify(transactionService).getRevenueReport(1L);
    }
}