package com.revpay.controller;

import com.revpay.dto.ApiResponse;
import com.revpay.dto.RepayLoanRequest;
import com.revpay.repository.LoanRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revpay.dto.ApplyLoanRequest;
import com.revpay.model.Loan;
import com.revpay.service.LoanService;

import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;

    private final LoanRepository loanRepository;

    public LoanController(LoanService loanService, LoanRepository loanRepository) {
        this.loanService = loanService;
        this.loanRepository = loanRepository;
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

    /**
     * GET LOANS FOR BUSINESS
     * This fetches all loans (Approved, Disbursed, etc.) for a specific business ID
     */
    @GetMapping("/business/{businessId}")
    public ResponseEntity<ApiResponse<List<Loan>>> getLoansByBusiness(@PathVariable Long businessId) {
        // Using the underscore method we just added to the repository
        List<Loan> loans = loanRepository.findByBusiness_UserId(businessId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Business loans fetched successfully", loans)
        );
    }
}