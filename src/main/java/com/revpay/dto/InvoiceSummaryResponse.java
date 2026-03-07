package com.revpay.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class InvoiceSummaryResponse {

    private BigDecimal totalPaid;
    private BigDecimal totalPending;
    private BigDecimal totalOverdue;
}