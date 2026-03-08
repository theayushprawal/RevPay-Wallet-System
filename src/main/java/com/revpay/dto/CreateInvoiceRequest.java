package com.revpay.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvoiceRequest {

    private Long businessId;
    private Long customerId;
    private String customerName;
    private LocalDate dueDate;
    private List<InvoiceItemRequest> items;
}