package com.revpay.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepayLoanRequest {

    private Long loanId;
    private Long businessId;
    private BigDecimal amount; // EMI amount
}