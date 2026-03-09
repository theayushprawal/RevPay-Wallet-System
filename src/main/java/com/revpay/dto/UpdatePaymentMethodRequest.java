package com.revpay.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePaymentMethodRequest {

    @NotNull(message = "UserId is required")
    private Long userId;
    private String paymentName;
    private String billingAddress;
    private LocalDate expiryDate; // optional, CARD only
}