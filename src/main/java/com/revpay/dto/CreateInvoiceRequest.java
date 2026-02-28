package com.revpay.dto;

import java.time.LocalDate;
import java.util.List;

public class CreateInvoiceRequest {

    // Business creating the invoice
    private Long businessId;

    // Customer info (simplified)
    private Long customerId;
    private String customerName;

    // Invoice details
    private LocalDate dueDate;

    // Items
    private List<InvoiceItemRequest> items;

    public CreateInvoiceRequest() {}

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public List<InvoiceItemRequest> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItemRequest> items) {
        this.items = items;
    }
}