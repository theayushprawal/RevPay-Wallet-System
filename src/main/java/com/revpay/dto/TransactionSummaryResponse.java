package com.revpay.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class TransactionSummaryResponse {

    private BigDecimal totalSent;
    private BigDecimal totalReceived;
    private BigDecimal pendingRequests;
}