package com.revpay.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopCustomerResponse {

    private Long customerId;
    private String customerName;
    private Long totalTransactions;
    private BigDecimal totalRevenue;
}