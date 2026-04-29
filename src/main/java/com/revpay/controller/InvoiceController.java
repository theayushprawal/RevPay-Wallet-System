package com.revpay.controller;

import java.util.List;

import com.revpay.dto.ApiResponse;
import com.revpay.dto.InvoiceSummaryResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('BUSINESS') and @securityGuard.isUserMatching(authentication, #request.businessId)")
    @PostMapping
    public ResponseEntity<ApiResponse<Invoice>> createInvoice(
            @Valid @RequestBody CreateInvoiceRequest request) {

        Invoice invoice = invoiceService.createInvoice(request);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Invoice created successfully", invoice)
        );
    }

    /**
     * SEND INVOICE (DRAFT -> SENT)
     */
    // IDOR protection via isInvoiceOwner
    @PreAuthorize("hasAuthority('BUSINESS') and @securityGuard.isInvoiceOwner(authentication, #invoiceId)")
    @PostMapping("/{invoiceId}/send")
    public ResponseEntity<ApiResponse<Void>> sendInvoice(
            @PathVariable Long invoiceId) {

        invoiceService.sendInvoice(invoiceId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Invoice sent successfully", null)
        );
    }

    /**
     * PAY INVOICE (SENT -> PAID)
     */
    // Allowed BUSINESS accounts to pay invoices (B2B), plus IDOR check on customerId
    @PreAuthorize("hasAnyAuthority('PERSONAL', 'BUSINESS') and @securityGuard.isUserMatching(authentication, #customerId)")
    @PostMapping("/{invoiceId}/pay")
    public ResponseEntity<ApiResponse<Void>> payInvoice(
            @PathVariable Long invoiceId,
            @RequestParam Long customerId,
            @RequestParam String transactionPin) {

        invoiceService.payInvoice(invoiceId, customerId, transactionPin);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Invoice paid successfully", null)
        );
    }

    /**
     * CANCEL INVOICE
     */
    // IDOR protection via isInvoiceOwner
    @PreAuthorize("hasAuthority('BUSINESS') and @securityGuard.isInvoiceOwner(authentication, #invoiceId)")
    @PostMapping("/{invoiceId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelInvoice(
            @PathVariable Long invoiceId) {

        invoiceService.cancelInvoice(invoiceId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Invoice cancelled successfully", null)
        );
    }

    /**
     * GET INVOICES FOR BUSINESS (Invoices Issued)
     */
    @PreAuthorize("hasAuthority('BUSINESS') and @securityGuard.isUserMatching(authentication, #businessId)")
    @GetMapping("/business/{businessId}")
    public ResponseEntity<ApiResponse<List<Invoice>>> getInvoicesForBusiness(
            @PathVariable Long businessId) {

        List<Invoice> invoices = invoiceService.getInvoicesForBusiness(businessId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Business invoices fetched successfully", invoices)
        );
    }

    /**
     * GET INVOICES FOR CUSTOMER (Invoices Received)
     */
    // Allowed BUSINESS accounts to view received invoices (B2B)
    @PreAuthorize("hasAnyAuthority('PERSONAL', 'BUSINESS') and @securityGuard.isUserMatching(authentication, #customerId)")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<Invoice>>> getInvoicesForCustomer(
            @PathVariable Long customerId) {

        List<Invoice> invoices = invoiceService.getInvoicesForCustomer(customerId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Customer invoices fetched successfully", invoices)
        );
    }

    /**
     * GET OUTSTANDING INVOICE SUMMARY
     */
    @PreAuthorize("hasAuthority('BUSINESS') and @securityGuard.isUserMatching(authentication, #businessId)")
    @GetMapping("/summary/{businessId}")
    public ResponseEntity<ApiResponse<InvoiceSummaryResponse>> getInvoiceSummary(
            @PathVariable Long businessId) {

        InvoiceSummaryResponse summary = invoiceService.getInvoiceSummary(businessId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Invoice summary fetched successfully", summary)
        );
    }
}