package com.revpay.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvoiceRequest {

    @NotNull(message = "BusinessId is required")
    private Long businessId;

    @NotNull(message = "CustomerId is required")
    private Long customerId;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    @NotEmpty(message = "Invoice must contain at least one item")
    @Valid
    private List<InvoiceItemRequest> items;
}