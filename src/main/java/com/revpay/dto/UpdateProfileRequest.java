package com.revpay.dto;

import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @NotNull(message = "UserId is required")
    private Long userId;

    private String fullName;

    private String email;

    private String phone;

    // Business fields
    private String businessName;

    private String businessType;

    private String address;
}