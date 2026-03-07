package com.revpay.service;

import java.math.BigDecimal;
import java.util.List;

import com.revpay.dto.RevenueReportResponse;
import com.revpay.dto.TopCustomerResponse;
import com.revpay.dto.TransactionFilterRequest;
import com.revpay.dto.TransactionSummaryResponse;
import com.revpay.model.Transaction;
import org.springframework.data.domain.Page;

public interface TransactionService {

    /**
     * Send money from one user to another.
     */
    void sendMoney(Long senderId,
                   Long receiverId,
                   BigDecimal amount,
                   String transactionPin,
                   String remarks);

    /**
     * Get all transactions related to a user
     * (sent + received).
     */
    List<Transaction> getTransactionsForUser(Long userId);


    Page<Transaction> filterTransactionsPaged(
            TransactionFilterRequest request,
            int page,
            int size
    );

    byte[] exportTransactionsToCsv(Long userId);

    // To fetch transaction summary
    TransactionSummaryResponse getTransactionSummary(Long userId);

    // To fetch top customer and payment trends
    List<TopCustomerResponse> getTopCustomers(Long businessId);

    //For fetching revenue report (daily/weekly/monthly)
    RevenueReportResponse getRevenueReport(Long businessId);
}