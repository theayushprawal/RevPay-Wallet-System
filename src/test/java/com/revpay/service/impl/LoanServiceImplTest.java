package com.revpay.service.impl;

import com.revpay.dto.ApplyLoanRequest;
import com.revpay.dto.RepayLoanRequest;
import com.revpay.model.*;
import com.revpay.model.enums.*;
import com.revpay.repository.*;
import com.revpay.service.AuthService;
import com.revpay.service.NotificationService;

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

    @Mock
    private AuthService authService;

    @InjectMocks
    private LoanServiceImpl loanService;

    private User business;
    private Wallet wallet;
    private Loan loan;

    @BeforeEach
    void setup() {

        business = new User();
        business.setUserId(1L);
        business.setUserType(UserType.BUSINESS);
        business.setFullName("Business User");

        wallet = new Wallet();
        wallet.setUser(business);
        wallet.setBalance(new BigDecimal("10000"));

        loan = new Loan();
        loan.setLoanId(1L);
        loan.setBusiness(business);
        loan.setAmount(new BigDecimal("50000"));
        loan.setStatus(LoanStatus.APPROVED);
        loan.setTenureMonths(12);
        loan.setEmi(new BigDecimal("5000"));
    }

    // APPLY LOAN SUCCESS
    @Test
    void testApplyLoanSuccess() {

        ApplyLoanRequest request = new ApplyLoanRequest();
        request.setBusinessId(1L);
        request.setAmount(new BigDecimal("50000"));
        request.setTenureMonths(12);
        request.setDocumentName("doc.pdf");

        when(userRepository.findById(1L)).thenReturn(Optional.of(business));
        when(loanRepository.save(any())).thenReturn(loan);

        Loan result = loanService.applyLoan(request);

        assertNotNull(result);
        verify(loanRepository).save(any(Loan.class));
    }

    // APPLY LOAN INVALID AMOUNT
    @Test
    void testApplyLoanInvalidAmount() {

        ApplyLoanRequest request = new ApplyLoanRequest();
        request.setBusinessId(1L);
        request.setAmount(new BigDecimal("5000"));
        request.setTenureMonths(12);
        request.setDocumentName("doc.pdf");

        assertThrows(IllegalArgumentException.class,
                () -> loanService.applyLoan(request));
    }

    // DISBURSE LOAN SUCCESS
    @Test
    void testDisburseLoanSuccess() {

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(walletRepository.findByUser(business)).thenReturn(Optional.of(wallet));

        loanService.disburseLoan(1L);

        verify(walletRepository).save(any());
        verify(transactionRepository).save(any());
        verify(notificationService).sendNotification(
                eq(1L),
                anyString(),
                eq(NotificationType.LOAN)
        );
    }

    // DISBURSE LOAN NOT APPROVED
    @Test
    void testDisburseLoanNotApproved() {

        loan.setStatus(LoanStatus.REJECTED);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        assertThrows(IllegalStateException.class,
                () -> loanService.disburseLoan(1L));
    }

    // REPAY LOAN SUCCESS
    @Test
    void testRepayLoanSuccess() {

        loan.setStatus(LoanStatus.DISBURSED);

        RepayLoanRequest request = new RepayLoanRequest();
        request.setLoanId(1L);
        request.setBusinessId(1L);
        request.setAmount(new BigDecimal("5000"));
        request.setTransactionPin("1234");

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(authService.verifyTransactionPin(business, "1234")).thenReturn(true);
        when(walletRepository.findByUser(business)).thenReturn(Optional.of(wallet));
        when(repaymentScheduleRepository.findByLoan(loan))
                .thenReturn(List.of());

        loanService.repayLoan(request);

        verify(walletRepository).save(any());
        verify(transactionRepository).save(any());
        verify(repaymentScheduleRepository).save(any());
    }

    // REPAY LOAN INVALID PIN
    @Test
    void testRepayLoanInvalidPin() {

        loan.setStatus(LoanStatus.DISBURSED);

        RepayLoanRequest request = new RepayLoanRequest();
        request.setLoanId(1L);
        request.setBusinessId(1L);
        request.setAmount(new BigDecimal("5000"));
        request.setTransactionPin("1234");

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(authService.verifyTransactionPin(business, "1234")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> loanService.repayLoan(request));
    }

    // REPAY LOAN INSUFFICIENT BALANCE
    @Test
    void testRepayLoanInsufficientBalance() {

        loan.setStatus(LoanStatus.DISBURSED);
        wallet.setBalance(new BigDecimal("100"));

        RepayLoanRequest request = new RepayLoanRequest();
        request.setLoanId(1L);
        request.setBusinessId(1L);
        request.setAmount(new BigDecimal("5000"));
        request.setTransactionPin("1234");

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(authService.verifyTransactionPin(business, "1234")).thenReturn(true);
        when(walletRepository.findByUser(business)).thenReturn(Optional.of(wallet));

        assertThrows(IllegalStateException.class,
                () -> loanService.repayLoan(request));
    }
}