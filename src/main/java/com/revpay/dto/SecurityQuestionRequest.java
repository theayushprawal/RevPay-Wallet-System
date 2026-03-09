package com.revpay.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityQuestionRequest {

    @NotBlank(message = "Security question is required")
    private String question;

    @NotBlank(message = "Security answer is required")
    private String answer;
}