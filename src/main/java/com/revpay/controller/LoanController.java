package com.revpay.controller;

import com.revpay.dto.RepayLoanRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revpay.dto.ApplyLoanRequest;
import com.revpay.model.Loan;
import com.revpay.service.LoanService;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    /**
     * Apply for a loan
     */
    @PostMapping("/apply")
    public ResponseEntity<Loan> applyLoan(
            @RequestBody ApplyLoanRequest request) {

        Loan loan = loanService.applyLoan(request);
        return ResponseEntity.ok(loan);
    }

    /**
     * Disburse approved loan
     */
    @PostMapping("/{loanId}/disburse")
    public ResponseEntity<String> disburseLoan(
            @PathVariable Long loanId) {

        loanService.disburseLoan(loanId);
        return ResponseEntity.ok("Loan disbursed successfully");
    }

    @PostMapping("/repay")
    public ResponseEntity<String> repayLoan(
            @RequestBody RepayLoanRequest request) {

        loanService.repayLoan(request);
        return ResponseEntity.ok("Loan repayment successful");
    }
}