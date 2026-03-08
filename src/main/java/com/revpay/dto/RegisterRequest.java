package com.revpay.dto;

import com.revpay.model.enums.UserType;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    // BASIC USER DETAILS
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private UserType userType;

    // SECURITY
    private SecurityQuestionRequest securityQuestion;
    private String transactionPin;

    // BUSINESS DETAILS
    private String businessName;
    private String businessType;
    private String panNumber;
    private String address;
    private String verificationDocument;
}