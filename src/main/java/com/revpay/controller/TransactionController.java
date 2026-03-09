package com.revpay.controller;

import java.math.BigDecimal;
import java.util.List;

import com.revpay.dto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revpay.model.Transaction;
import com.revpay.service.TransactionService;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * SEND MONEY
     */
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendMoney(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam BigDecimal amount,
            @RequestParam String transactionPin,
            @RequestParam(required = false) String remarks) {

        transactionService.sendMoney(
                senderId,
                receiverId,
                amount,
                transactionPin,
                remarks
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Money sent successfully",
                        null
                )
        );
    }

    /**
     * TRANSACTION HISTORY
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<ApiResponse<List<Transaction>>> getTransactionHistory(
            @PathVariable Long userId) {

        List<Transaction> transactions =
                transactionService.getTransactionsForUser(userId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Transaction history fetched successfully",
                        transactions
                )
        );
    }

    /**
     * FILTER + PAGINATED HISTORY
     */
    @PostMapping("/filter/paged")
    public ResponseEntity<ApiResponse<Page<Transaction>>> filterTransactionsPaged(
            @Valid @RequestBody TransactionFilterRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Transaction> result =
                transactionService.filterTransactionsPaged(request, page, size);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Filtered transactions fetched successfully",
                        result
                )
        );
    }

    /**
     * EXPORT TRANSACTIONS (CSV)
     */
    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportTransactionsCsv(
            @RequestParam Long userId) {

        byte[] csvData = transactionService.exportTransactionsToCsv(userId);

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=transactions.csv")
                .header("Content-Type", "text/csv")
                .body(csvData);
    }

    /**
     * TRANSACTION SUMMARY
     */
    @GetMapping("/summary/{userId}")
    public ResponseEntity<ApiResponse<TransactionSummaryResponse>> getTransactionSummary(
            @PathVariable Long userId) {

        TransactionSummaryResponse summary =
                transactionService.getTransactionSummary(userId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Transaction summary fetched successfully",
                        summary
                )
        );
    }

    /**
     * TOP CUSTOMERS ANALYTICS
     */
    @GetMapping("/top-customers/{businessId}")
    public ResponseEntity<ApiResponse<List<TopCustomerResponse>>> getTopCustomers(
            @PathVariable Long businessId) {

        List<TopCustomerResponse> customers =
                transactionService.getTopCustomers(businessId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Top customers fetched successfully",
                        customers
                )
        );
    }

    /**
     * REVENUE REPORT
     */
    @GetMapping("/revenue-report/{businessId}")
    public ResponseEntity<ApiResponse<RevenueReportResponse>> getRevenueReport(
            @PathVariable Long businessId) {

        RevenueReportResponse report =
                transactionService.getRevenueReport(businessId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Revenue report fetched successfully",
                        report
                )
        );
    }
}