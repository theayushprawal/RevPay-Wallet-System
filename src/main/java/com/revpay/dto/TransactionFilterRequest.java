package com.revpay.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.revpay.model.enums.TransactionStatus;
import com.revpay.model.enums.TransactionType;

public class TransactionFilterRequest {

    // Required: whose transactions
    private Long userId;

    // Filters
    private TransactionType txnType;
    private TransactionStatus status;

    // Date range
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // Amount range
    private BigDecimal minAmount;
    private BigDecimal maxAmount;

    // Search: counterparty name OR transaction ID
    private String searchTerm;

    public TransactionFilterRequest() {}

    // ===== GETTERS & SETTERS =====

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public TransactionType getTxnType() {
        return txnType;
    }

    public void setTxnType(TransactionType txnType) {
        this.txnType = txnType;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
}