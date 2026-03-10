package com.revpay.controller;

import com.revpay.dto.ApiResponse;
import com.revpay.dto.ApplyLoanRequest;
import com.revpay.dto.RepayLoanRequest;
import com.revpay.model.Loan;
import com.revpay.service.LoanService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanControllerTest {

    @Mock
    private LoanService loanService;

    @InjectMocks
    private LoanController loanController;

    private Loan loan;
    private ApplyLoanRequest applyLoanRequest;
    private RepayLoanRequest repayLoanRequest;

    @BeforeEach
    void setUp() {

        loan = new Loan();

        applyLoanRequest = new ApplyLoanRequest();

        repayLoanRequest = new RepayLoanRequest();
    }

    /**
     * Test Apply Loan
     */
    @Test
    void testApplyLoan() {

        when(loanService.applyLoan(applyLoanRequest)).thenReturn(loan);

        ResponseEntity<ApiResponse<Loan>> response =
                loanController.applyLoan(applyLoanRequest);

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Loan application submitted successfully",
                response.getBody().getMessage());
        assertEquals(loan, response.getBody().getData());

        verify(loanService, times(1)).applyLoan(applyLoanRequest);
    }

    /**
     * Test Disburse Loan
     */
    @Test
    void testDisburseLoan() {

        doNothing().when(loanService).disburseLoan(1L);

        ResponseEntity<ApiResponse<Void>> response =
                loanController.disburseLoan(1L);

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Loan disbursed successfully",
                response.getBody().getMessage());

        verify(loanService, times(1)).disburseLoan(1L);
    }

    /**
     * Test Repay Loan
     */
    @Test
    void testRepayLoan() {

        doNothing().when(loanService).repayLoan(repayLoanRequest);

        ResponseEntity<ApiResponse<Void>> response =
                loanController.repayLoan(repayLoanRequest);

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Loan repayment successful",
                response.getBody().getMessage());

        verify(loanService, times(1)).repayLoan(repayLoanRequest);
    }
}