package com.revpay.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revpay.dto.CreateInvoiceRequest;
import com.revpay.model.Invoice;
import com.revpay.service.InvoiceService;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    /**
     * CREATE INVOICE (DRAFT)
     */
    @PostMapping
    public ResponseEntity<Invoice> createInvoice(
            @RequestBody CreateInvoiceRequest request) {

        Invoice invoice = invoiceService.createInvoice(request);
        return ResponseEntity.ok(invoice);
    }

    /**
     * SEND INVOICE (DRAFT -> SENT)
     */
    @PostMapping("/{invoiceId}/send")
    public ResponseEntity<String> sendInvoice(
            @PathVariable Long invoiceId) {

        invoiceService.sendInvoice(invoiceId);
        return ResponseEntity.ok("Invoice sent successfully");
    }

    /**
     * PAY INVOICE (SENT -> PAID)
     */
    @PostMapping("/{invoiceId}/pay")
    public ResponseEntity<String> payInvoice(
            @PathVariable Long invoiceId,
            @RequestParam Long customerId,
            @RequestParam String transactionPin) {

        invoiceService.payInvoice(invoiceId, customerId, transactionPin);
        return ResponseEntity.ok("Invoice paid successfully");
    }

    /**
     * CANCEL INVOICE
     */
    @PostMapping("/{invoiceId}/cancel")
    public ResponseEntity<String> cancelInvoice(
            @PathVariable Long invoiceId) {

        invoiceService.cancelInvoice(invoiceId);
        return ResponseEntity.ok("Invoice cancelled successfully");
    }

    /**
     * GET INVOICES FOR BUSINESS
     */
    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<Invoice>> getInvoicesForBusiness(
            @PathVariable Long businessId) {

        return ResponseEntity.ok(
                invoiceService.getInvoicesForBusiness(businessId)
        );
    }

    /**
     * GET INVOICES FOR CUSTOMER
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Invoice>> getInvoicesForCustomer(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                invoiceService.getInvoicesForCustomer(customerId)
        );
    }
}