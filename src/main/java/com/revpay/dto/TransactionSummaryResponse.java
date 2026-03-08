package com.revpay.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSummaryResponse {

    private BigDecimal totalSent;
    private BigDecimal totalReceived;
    private BigDecimal pendingRequests;
}