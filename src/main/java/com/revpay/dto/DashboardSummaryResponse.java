package com.revpay.dto;

import java.math.BigDecimal;
import java.util.List;

import com.revpay.model.Transaction;
import com.revpay.model.MoneyRequest;
import lombok.Data;

@Data
public class DashboardSummaryResponse {

    private BigDecimal walletBalance;

    private List<Transaction> recentTransactions;

    private List<MoneyRequest> pendingRequests;

    private Long unreadNotifications;
}