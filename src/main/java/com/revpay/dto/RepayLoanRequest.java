package com.revpay.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepayLoanRequest {

    @NotNull(message = "LoanId is required")
    private Long loanId;

    @NotNull(message = "BusinessId is required")
    private Long businessId;

    @NotNull(message = "Repayment amount is required")
    @Positive(message = "Repayment amount must be greater than zero")
    private BigDecimal amount;
}