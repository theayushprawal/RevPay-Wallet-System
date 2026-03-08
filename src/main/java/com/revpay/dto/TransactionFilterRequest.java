package com.revpay.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.revpay.model.enums.TransactionStatus;
import com.revpay.model.enums.TransactionType;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionFilterRequest {

    private Long userId;

    private TransactionType txnType;
    private TransactionStatus status;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private BigDecimal minAmount;
    private BigDecimal maxAmount;

    private String searchTerm;
}