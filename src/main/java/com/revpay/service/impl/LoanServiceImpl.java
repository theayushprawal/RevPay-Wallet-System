package com.revpay.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.revpay.dto.RepayLoanRequest;
import com.revpay.model.*;
import com.revpay.model.enums.*;
import com.revpay.repository.*;
import com.revpay.service.NotificationService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revpay.dto.ApplyLoanRequest;
import com.revpay.service.LoanService;

@Service
@Transactional
public class LoanServiceImpl implements LoanService {

    private static final Logger log = LogManager.getLogger(LoanServiceImpl.class);

    private static final BigDecimal MIN_LOAN_AMOUNT =
            new BigDecimal("10000");

    private static final BigDecimal AUTO_APPROVAL_LIMIT =
            new BigDecimal("500000");

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;
    private final RepaymentScheduleRepository repaymentScheduleRepository;

    public LoanServiceImpl(LoanRepository loanRepository,
                           UserRepository userRepository,
                           WalletRepository walletRepository,
                           TransactionRepository transactionRepository,
                           NotificationService notificationService,
                           RepaymentScheduleRepository repaymentScheduleRepository) {

        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.notificationService = notificationService;
        this.repaymentScheduleRepository = repaymentScheduleRepository;
    }

    @Override
    public Loan applyLoan(ApplyLoanRequest request) {

        log.info("Loan application received for businessId={} amount={}",
                request != null ? request.getBusinessId() : null,
                request != null ? request.getAmount() : null);

        if (request == null || request.getBusinessId() == null) {
            log.warn("Loan application failed: invalid request");
            throw new IllegalArgumentException("Invalid loan request");
        }

        if (request.getAmount() == null
                || request.getAmount().compareTo(MIN_LOAN_AMOUNT) < 0) {
            log.warn("Loan application failed: amount below minimum businessId={}",
                    request.getBusinessId());
            throw new IllegalArgumentException(
                    "Minimum loan amount is ₹10,000"
            );
        }

        if (request.getTenureMonths() == null || request.getTenureMonths() <= 0) {
            log.warn("Loan application failed: invalid tenure businessId={}",
                    request.getBusinessId());
            throw new IllegalArgumentException("Invalid tenure");
        }

        if (request.getDocumentName() == null || request.getDocumentName().isBlank()) {
            log.warn("Loan application failed: document missing businessId={}",
                    request.getBusinessId());
            throw new IllegalArgumentException("Loan document is required");
        }

        // Fetch business user
        User business = userRepository.findById(request.getBusinessId())
                .orElseThrow(() -> {
                    log.warn("Loan application failed: business user not found businessId={}",
                            request.getBusinessId());
                    return new IllegalArgumentException("Business user not found");
                });

        if (business.getUserType() != UserType.BUSINESS) {
            log.warn("Loan application failed: user not business userId={}",
                    business.getUserId());
            throw new IllegalStateException("Only business users can apply for loans");
        }

        // Create loan
        Loan loan = new Loan();
        loan.setBusiness(business);
        loan.setAmount(request.getAmount());
        loan.setTenureMonths(request.getTenureMonths());
        loan.setPurpose(request.getPurpose());
        loan.setCreatedAt(LocalDateTime.now());
        loan.setDocumentName(request.getDocumentName());
        loan.setDocumentUploaded(YesNoStatus.YES);

        // Auto approval logic
        if (request.getAmount().compareTo(AUTO_APPROVAL_LIMIT) <= 0
                && loan.getDocumentUploaded() == YesNoStatus.YES) {
            loan.setStatus(LoanStatus.APPROVED);
            log.info("Loan auto-approved businessId={} amount={}",
                    business.getUserId(), loan.getAmount());
        } else {
            loan.setStatus(LoanStatus.REJECTED);
            log.info("Loan auto-rejected businessId={} amount={}",
                    business.getUserId(), loan.getAmount());
        }

        Loan savedLoan = loanRepository.save(loan);

        log.info("Loan application saved loanId={} status={}",
                savedLoan.getLoanId(), savedLoan.getStatus());

        return savedLoan;
    }

    @Override
    @Transactional
    public void disburseLoan(Long loanId) {

        log.info("Loan disbursement requested loanId={}", loanId);

        if (loanId == null) {
            throw new IllegalArgumentException("LoanId is required");
        }

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> {
                    log.warn("Loan disbursement failed: loan not found loanId={}", loanId);
                    return new IllegalArgumentException("Loan not found");
                });

        // Only APPROVED loans can be disbursed
        if (loan.getStatus() != LoanStatus.APPROVED) {
            log.warn("Loan disbursement failed: loan not approved loanId={} status={}",
                    loanId, loan.getStatus());
            throw new IllegalStateException("Loan is not approved for disbursement");
        }

        User business = loan.getBusiness();

        // Fetch wallet
        Wallet wallet = walletRepository.findByUser(business)
                .orElseThrow(() -> {
                    log.error("Loan disbursement failed: wallet not found userId={}",
                            business.getUserId());
                    return new IllegalStateException("Wallet not found");
                });

        // Credit wallet
        wallet.setBalance(wallet.getBalance().add(loan.getAmount()));
        wallet.setLastUpdated(LocalDateTime.now());
        walletRepository.save(wallet);

        // Create transaction
        Transaction txn = new Transaction();
        txn.setSender(null); // system
        txn.setReceiver(business);
        txn.setAmount(loan.getAmount());
        txn.setTxnType(TransactionType.LOAN_CREDIT);
        txn.setStatus(TransactionStatus.SUCCESS);
        txn.setRemarks("Loan disbursed");
        txn.setTxnDate(LocalDateTime.now());

        transactionRepository.save(txn);

        // Update loan status
        loan.setStatus(LoanStatus.DISBURSED);
        loanRepository.save(loan);

        log.info("Loan disbursed successfully loanId={} amount={} businessId={}",
                loanId, loan.getAmount(), business.getUserId());

        // Optional notification (recommended)
        notificationService.sendNotification(
                business.getUserId(),
                "Loan amount ₹" + loan.getAmount() + " has been credited to your wallet",
                NotificationType.LOAN
        );
    }

    @Override
    @Transactional
    public void repayLoan(RepayLoanRequest request) {

        log.info("Loan repayment requested loanId={} businessId={} amount={}",
                request != null ? request.getLoanId() : null,
                request != null ? request.getBusinessId() : null,
                request != null ? request.getAmount() : null);

        if (request == null
                || request.getLoanId() == null
                || request.getBusinessId() == null
                || request.getAmount() == null
                || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid repayment request");
        }

        Loan loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() -> {
                    log.warn("Loan repayment failed: loan not found loanId={}",
                            request.getLoanId());
                    return new IllegalArgumentException("Loan not found");
                });

        if (loan.getStatus() != LoanStatus.DISBURSED) {
            log.warn("Loan repayment failed: loan not active loanId={} status={}",
                    loan.getLoanId(), loan.getStatus());
            throw new IllegalStateException("Loan is not active for repayment");
        }

        User business = loan.getBusiness();

        if (!business.getUserId().equals(request.getBusinessId())) {
            log.warn("Loan repayment failed: business mismatch loanId={} businessId={}",
                    loan.getLoanId(), request.getBusinessId());
            throw new IllegalStateException("Loan does not belong to this business");
        }

        // Fetch wallet
        Wallet wallet = walletRepository.findByUser(business)
                .orElseThrow(() -> {
                    log.error("Loan repayment failed: wallet not found businessId={}",
                            business.getUserId());
                    return new IllegalStateException("Wallet not found");
                });

        // Check wallet balance
        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            log.warn("Loan repayment failed: insufficient wallet balance businessId={} balance={} amount={}",
                    business.getUserId(), wallet.getBalance(), request.getAmount());
            throw new IllegalStateException("Insufficient wallet balance");
        }

        // Debit wallet
        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        wallet.setLastUpdated(LocalDateTime.now());
        walletRepository.save(wallet);

        // Create transaction
        Transaction txn = new Transaction();
        txn.setSender(business);
        txn.setReceiver(null); // system
        txn.setAmount(request.getAmount());
        txn.setTxnType(TransactionType.LOAN_REPAYMENT);
        txn.setStatus(TransactionStatus.SUCCESS);
        txn.setRemarks("Loan EMI repayment");
        txn.setTxnDate(LocalDateTime.now());
        transactionRepository.save(txn);

        // Create repayment schedule entry
        RepaymentSchedule repayment = new RepaymentSchedule();
        repayment.setLoan(loan);
        repayment.setEmi(request.getAmount());
        repayment.setPaymentDate(LocalDateTime.now());
        repayment.setStatus("PAID");
        repayment.setRemarks("EMI paid");
        repaymentScheduleRepository.save(repayment);

        // Check if loan fully repaid
        BigDecimal totalPaid = repaymentScheduleRepository.findByLoan(loan)
                .stream()
                .map(RepaymentSchedule::getEmi)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPaid.compareTo(loan.getAmount()) >= 0) {
            loan.setStatus(LoanStatus.CLOSED);
            loanRepository.save(loan);

            log.info("Loan fully repaid and closed loanId={}", loan.getLoanId());

            notificationService.sendNotification(
                    business.getUserId(),
                    "Loan fully repaid and closed",
                    NotificationType.LOAN
            );
        }
    }
}