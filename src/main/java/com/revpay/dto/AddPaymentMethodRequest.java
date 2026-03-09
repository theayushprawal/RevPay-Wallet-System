package com.revpay.dto;

import java.time.LocalDate;

import com.revpay.model.enums.PaymentMethodType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddPaymentMethodRequest {

    @NotNull(message = "UserId is required")
    private Long userId;

    @NotNull(message = "Payment method type is required")
    private PaymentMethodType methodType;

    @NotBlank(message = "Payment name is required")
    private String paymentName;

    @NotBlank(message = "Billing address is required")
    private String billingAddress;

    private Boolean isDefault;

    // CARD
    private String cardNumber;

    @Pattern(regexp = "\\d{3}", message = "CVV must be 3 digits")
    private String cvv;

    private LocalDate expiryDate;

    // BANK
    private String ifscCode;
    private String accountNumber;
}