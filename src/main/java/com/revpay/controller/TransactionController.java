package com.revpay.controller;

import java.math.BigDecimal;
import java.util.List;

import com.revpay.dto.RevenueReportResponse;
import com.revpay.dto.TopCustomerResponse;
import com.revpay.dto.TransactionFilterRequest;
import com.revpay.dto.TransactionSummaryResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<String> sendMoney(
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

        return ResponseEntity.ok("Money sent successfully");
    }

    /**
     * TRANSACTION HISTORY
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<Transaction>> getTransactionHistory(
            @PathVariable Long userId) {

        List<Transaction> transactions =
                transactionService.getTransactionsForUser(userId);

        return ResponseEntity.ok(transactions);
    }

    /**
     * FILTER + PAGINATED HIST
     */
    @PostMapping("/filter/paged")
    public ResponseEntity<Page<Transaction>> filterTransactionsPaged(
            @RequestBody TransactionFilterRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Transaction> result =
                transactionService.filterTransactionsPaged(request, page, size);

        return ResponseEntity.ok(result);
    }

    //for exporting transaction in csv file
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

    //to get transaction summary
    @GetMapping("/summary/{userId}")
    public ResponseEntity<TransactionSummaryResponse> getTransactionSummary(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                transactionService.getTransactionSummary(userId)
        );
    }

    //for getting top customer analytics
    @GetMapping("/top-customers/{businessId}")
    public ResponseEntity<List<TopCustomerResponse>> getTopCustomers(
            @PathVariable Long businessId) {

        return ResponseEntity.ok(
                transactionService.getTopCustomers(businessId)
        );
    }

    //For fetching revenue report (daily/weekly/monthly)
    @GetMapping("/revenue-report/{businessId}")
    public ResponseEntity<RevenueReportResponse> getRevenueReport(
            @PathVariable Long businessId) {

        return ResponseEntity.ok(
                transactionService.getRevenueReport(businessId)
        );
    }
}