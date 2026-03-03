package com.revpay.dto;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePaymentMethodRequest {

    private Long userId;
    private String paymentName;
    private String billingAddress;
    private LocalDate expiryDate; // optional, CARD only
}