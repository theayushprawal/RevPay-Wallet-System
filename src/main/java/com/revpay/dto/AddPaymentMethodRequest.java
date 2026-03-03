package com.revpay.dto;

import java.time.LocalDate;

import com.revpay.model.enums.PaymentMethodType;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddPaymentMethodRequest {

    private Long userId;

    private PaymentMethodType methodType; // CARD / BANK

    // Common fields
    private String paymentName;
    private String billingAddress;
    private Boolean isDefault;

    // Card-specific
    private String cardNumber;
    private String cvv;
    private LocalDate expiryDate;

    // Bank-specific
    private String ifscCode;
    private String accountNumber;
}