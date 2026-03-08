package com.revpay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    private Long userId;

    private String fullName;
    private String email;
    private String phone;

    // business fields (optional)
    private String businessName;
    private String businessType;
    private String address;
}