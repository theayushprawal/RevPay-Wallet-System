package com.revpay.controller;

import com.revpay.dto.ApiResponse;
import com.revpay.dto.RepayLoanRequest;
import jakarta.validation.Valid;
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
    public ResponseEntity<ApiResponse<Loan>> applyLoan(
            @Valid @RequestBody ApplyLoanRequest request) {

        Loan loan = loanService.applyLoan(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Loan application submitted successfully",
                        loan
                )
        );
    }

    /**
     * Disburse approved loan
     */
    @PostMapping("/{loanId}/disburse")
    public ResponseEntity<ApiResponse<Void>> disburseLoan(
            @PathVariable Long loanId) {

        loanService.disburseLoan(loanId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Loan disbursed successfully",
                        null
                )
        );
    }

    /**
     * Repay Loan
     */
    @PostMapping("/repay")
    public ResponseEntity<ApiResponse<Void>> repayLoan(
            @Valid @RequestBody RepayLoanRequest request) {

        loanService.repayLoan(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Loan repayment successful",
                        null
                )
        );
    }
}