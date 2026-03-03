package com.revpay.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplyLoanRequest {

    private Long businessId;
    private BigDecimal amount;
    private Integer tenureMonths;
    private String purpose;
}