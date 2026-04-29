package com.revpay.controller;

import com.revpay.dto.ApiResponse;
import com.revpay.dto.RepayLoanRequest;
import com.revpay.repository.LoanRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.revpay.dto.ApplyLoanRequest;
import com.revpay.model.Loan;
import com.revpay.service.LoanService;

import java.util.List;

@RestController
@RequestMapping("/loans")
@PreAuthorize("hasAuthority('BUSINESS')") // Blanket rule: Only businesses allowed here
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
    @PreAuthorize("hasAuthority('BUSINESS') and @securityGuard.isUserMatching(authentication, #request.businessId)")
    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<Loan>> applyLoan(
            @Valid @RequestBody ApplyLoanRequest request) {

        Loan loan = loanService.applyLoan(request);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Loan application submitted successfully", loan)
        );
    }

    /**
     * Disburse approved loan
     */
    // Explicitly added the IDOR check for loan ownership!
    @PreAuthorize("hasAuthority('BUSINESS') and @securityGuard.isLoanOwner(authentication, #loanId)")
    @PostMapping("/{loanId}/disburse")
    public ResponseEntity<ApiResponse<Void>> disburseLoan(
            @PathVariable Long loanId) {

        loanService.disburseLoan(loanId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Loan disbursed successfully", null)
        );
    }

    /**
     * Repay Loan
     */
    @PreAuthorize("hasAuthority('BUSINESS') and @securityGuard.isUserMatching(authentication, #request.businessId)")
    @PostMapping("/repay")
    public ResponseEntity<ApiResponse<Void>> repayLoan(
            @Valid @RequestBody RepayLoanRequest request) {

        loanService.repayLoan(request);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Loan repayment successful", null)
        );
    }

    /**
     * GET LOANS FOR BUSINESS
     */
    @PreAuthorize("hasAuthority('BUSINESS') and @securityGuard.isUserMatching(authentication, #businessId)")
    @GetMapping("/business/{businessId}")
    public ResponseEntity<ApiResponse<List<Loan>>> getLoansByBusiness(
            @PathVariable Long businessId) {

        List<Loan> loans = loanRepository.findByBusiness_UserId(businessId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Business loans fetched successfully", loans)
        );
    }
}