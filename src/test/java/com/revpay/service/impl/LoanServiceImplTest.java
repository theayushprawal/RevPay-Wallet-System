package com.revpay.service.impl;

import com.revpay.dto.ApplyLoanRequest;
import com.revpay.dto.RepayLoanRequest;
import com.revpay.model.*;
import com.revpay.model.enums.*;
import com.revpay.repository.*;
import com.revpay.service.NotificationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class LoanServiceImplTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private RepaymentScheduleRepository repaymentScheduleRepository;

    @InjectMocks
    private LoanServiceImpl loanService;

    private User business;
    private Wallet wallet;
    private Loan loan;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        business = new User();
        business.setUserId(1L);
        business.setUserType(UserType.BUSINESS);

        wallet = new Wallet();
        wallet.setUser(business);
        wallet.setBalance(new BigDecimal("50000"));
        wallet.setLastUpdated(LocalDateTime.now());

        loan = new Loan();
        loan.setLoanId(10L);
        loan.setBusiness(business);
        loan.setAmount(new BigDecimal("20000"));
        loan.setTenureMonths(12);
    }

    // --------------------------
    // APPLY LOAN
    // --------------------------

    @Test
    void testApplyLoanSuccess() {

        ApplyLoanRequest request = new ApplyLoanRequest();
        request.setBusinessId(1L);
        request.setAmount(new BigDecimal("20000"));
        request.setTenureMonths(12);
        request.setPurpose("Business expansion");
        request.setDocumentName("gst.pdf");

        when(userRepository.findById(1L)).thenReturn(Optional.of(business));
        when(loanRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Loan result = loanService.applyLoan(request);

        assertNotNull(result);
        assertEquals(LoanStatus.APPROVED, result.getStatus());

        verify(loanRepository).save(any());
    }

    @Test
    void testApplyLoanInvalidAmount() {

        ApplyLoanRequest request = new ApplyLoanRequest();
        request.setBusinessId(1L);
        request.setAmount(new BigDecimal("5000")); // below minimum
        request.setTenureMonths(12);
        request.setDocumentName("doc.pdf");

        assertThrows(IllegalArgumentException.class,
                () -> loanService.applyLoan(request));
    }

    @Test
    void testApplyLoanBusinessNotFound() {

        ApplyLoanRequest request = new ApplyLoanRequest();
        request.setBusinessId(1L);
        request.setAmount(new BigDecimal("20000"));
        request.setTenureMonths(12);
        request.setDocumentName("doc.pdf");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> loanService.applyLoan(request));
    }

    // --------------------------
    // DISBURSE LOAN
    // --------------------------

    @Test
    void testDisburseLoanSuccess() {

        loan.setStatus(LoanStatus.APPROVED);

        when(loanRepository.findById(10L)).thenReturn(Optional.of(loan));
        when(walletRepository.findByUser(business)).thenReturn(Optional.of(wallet));

        loanService.disburseLoan(10L);

        assertEquals(LoanStatus.DISBURSED, loan.getStatus());

        verify(walletRepository).save(any());
        verify(transactionRepository).save(any());
        verify(notificationService).sendNotification(anyLong(), anyString(), any());
    }

    @Test
    void testDisburseLoanNotApproved() {

        loan.setStatus(LoanStatus.REJECTED);

        when(loanRepository.findById(10L)).thenReturn(Optional.of(loan));

        assertThrows(IllegalStateException.class,
                () -> loanService.disburseLoan(10L));
    }

    // --------------------------
    // REPAY LOAN
    // --------------------------

    @Test
    void testRepayLoanSuccess() {

        loan.setStatus(LoanStatus.DISBURSED);

        RepayLoanRequest request = new RepayLoanRequest();
        request.setLoanId(10L);
        request.setBusinessId(1L);
        request.setAmount(new BigDecimal("5000"));

        when(loanRepository.findById(10L)).thenReturn(Optional.of(loan));
        when(walletRepository.findByUser(business)).thenReturn(Optional.of(wallet));

        when(repaymentScheduleRepository.findByLoan(loan))
                .thenReturn(List.of());

        loanService.repayLoan(request);

        verify(walletRepository).save(any());
        verify(transactionRepository).save(any());
        verify(repaymentScheduleRepository).save(any());
    }

    @Test
    void testRepayLoanInsufficientBalance() {

        loan.setStatus(LoanStatus.DISBURSED);

        wallet.setBalance(new BigDecimal("1000"));

        RepayLoanRequest request = new RepayLoanRequest();
        request.setLoanId(10L);
        request.setBusinessId(1L);
        request.setAmount(new BigDecimal("5000"));

        when(loanRepository.findById(10L)).thenReturn(Optional.of(loan));
        when(walletRepository.findByUser(business)).thenReturn(Optional.of(wallet));

        assertThrows(IllegalStateException.class,
                () -> loanService.repayLoan(request));
    }

    // --------------------------
    // LOAN CLOSED AFTER FULL PAYMENT
    // --------------------------

    @Test
    void testLoanClosedAfterFullRepayment() {

        loan.setStatus(LoanStatus.DISBURSED);

        RepayLoanRequest request = new RepayLoanRequest();
        request.setLoanId(10L);
        request.setBusinessId(1L);
        request.setAmount(new BigDecimal("20000"));

        RepaymentSchedule repayment = new RepaymentSchedule();
        repayment.setEmi(new BigDecimal("20000"));

        when(loanRepository.findById(10L)).thenReturn(Optional.of(loan));
        when(walletRepository.findByUser(business)).thenReturn(Optional.of(wallet));

        when(repaymentScheduleRepository.findByLoan(loan))
                .thenReturn(List.of(repayment));

        loanService.repayLoan(request);

        assertEquals(LoanStatus.CLOSED, loan.getStatus());

        verify(notificationService).sendNotification(anyLong(), anyString(), any());
    }
}

