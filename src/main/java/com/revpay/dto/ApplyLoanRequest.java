package com.revpay.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplyLoanRequest {

    @NotNull(message = "BusinessId is required")
    private Long businessId;

    @NotNull(message = "Loan amount is required")
    @Positive(message = "Loan amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Tenure is required")
    @Positive(message = "Tenure must be positive")
    private Integer tenureMonths;

    @NotBlank(message = "Loan purpose is required")
    private String purpose;

    @NotBlank(message = "Loan document is required")
    private String documentName;
}