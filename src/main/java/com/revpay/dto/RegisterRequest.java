package com.revpay.dto;

import com.revpay.model.enums.UserType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Password is required")
    private String password;

    @NotNull(message = "User type is required")
    private UserType userType;

    @Valid
    @NotNull(message = "Security question is required")
    private SecurityQuestionRequest securityQuestion;

    @NotBlank(message = "Transaction PIN is required")
    @Pattern(regexp = "\\d{4}", message = "Transaction PIN must be exactly 4 digits")
    private String transactionPin;

    // Business fields
    private String businessName;
    private String businessType;
    private String panNumber;
    private String address;
    private String verificationDocument;
}