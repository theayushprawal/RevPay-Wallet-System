package com.revpay.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopCustomerResponse {

    private Long customerId;
    private String customerName;
    private Long totalTransactions;
    private BigDecimal totalRevenue;
}