package com.revpay.service;

import java.util.List;

import com.revpay.dto.CreateInvoiceRequest;
import com.revpay.dto.InvoiceSummaryResponse;
import com.revpay.model.Invoice;

public interface InvoiceService {

    /**
     * Create a new invoice (DRAFT)
     */
    Invoice createInvoice(CreateInvoiceRequest request);

    /**
     * Send an invoice (DRAFT -> SENT)
     */
    void sendInvoice(Long invoiceId);

    /**
     * Pay an invoice (SENT -> PAID)
     */
    void payInvoice(Long invoiceId, Long customerId, String transactionPin);

    /**
     * Cancel an invoice (DRAFT / SENT -> CANCELLED)
     */
    void cancelInvoice(Long invoiceId);

    /**
     * View all invoices for a business
     */
    List<Invoice> getInvoicesForBusiness(Long businessId);

    /**
     * View all invoices for a customer
     */
    List<Invoice> getInvoicesForCustomer(Long customerId);

    //View all outstanding invoices
    InvoiceSummaryResponse getInvoiceSummary(Long businessId);
}