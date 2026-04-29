package com.revpay.controller;

import java.util.List;

import com.revpay.dto.ApiResponse;
import com.revpay.dto.UpdatePaymentMethodRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.revpay.dto.AddPaymentMethodRequest;
import com.revpay.model.PaymentMethod;
import com.revpay.service.PaymentMethodService;

@RestController
@RequestMapping("/payment-methods")
@PreAuthorize("hasAnyAuthority('PERSONAL', 'BUSINESS')") // Blanket rule: Both account types have payment methods
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    public PaymentMethodController(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    /**
     * ADD PAYMENT METHOD
     */
    // Assuming AddPaymentMethodRequest contains a userId field
    @PreAuthorize("hasAnyAuthority('PERSONAL', 'BUSINESS') and @securityGuard.isUserMatching(authentication, #request.userId)")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addPaymentMethod(
            @Valid @RequestBody AddPaymentMethodRequest request) {

        paymentMethodService.addPaymentMethod(request);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Payment method added successfully", null)
        );
    }

    /**
     * GET USER PAYMENT METHODS
     */
    @PreAuthorize("hasAnyAuthority('PERSONAL', 'BUSINESS') and @securityGuard.isUserMatching(authentication, #userId)")
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<PaymentMethod>>> getPaymentMethods(
            @PathVariable Long userId) {

        List<PaymentMethod> methods = paymentMethodService.getPaymentMethods(userId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Payment methods fetched successfully", methods)
        );
    }

    /**
     * SET DEFAULT PAYMENT METHOD
     */
    // Double protection: Verify the userId AND verify the payment method actually belongs to them
    @PreAuthorize("hasAnyAuthority('PERSONAL', 'BUSINESS') and @securityGuard.isUserMatching(authentication, #userId) and @securityGuard.isPaymentMethodOwner(authentication, #paymentMethodId)")
    @PostMapping("/{userId}/{paymentMethodId}/default")
    public ResponseEntity<ApiResponse<Void>> setDefault(
            @PathVariable Long userId,
            @PathVariable Long paymentMethodId) {

        paymentMethodService.setDefaultPaymentMethod(userId, paymentMethodId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Default payment method updated", null)
        );
    }

    /**
     * DELETE PAYMENT METHOD
     */
    // Double protection: Verify the userId AND verify the payment method actually belongs to them
    @PreAuthorize("hasAnyAuthority('PERSONAL', 'BUSINESS') and @securityGuard.isUserMatching(authentication, #userId) and @securityGuard.isPaymentMethodOwner(authentication, #paymentMethodId)")
    @DeleteMapping("/{userId}/{paymentMethodId}")
    public ResponseEntity<ApiResponse<Void>> deletePaymentMethod(
            @PathVariable Long userId,
            @PathVariable Long paymentMethodId) {

        paymentMethodService.deletePaymentMethod(userId, paymentMethodId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Payment method deleted", null)
        );
    }

    /**
     * UPDATE PAYMENT METHOD
     */
    // Protection: Verify the payment method belongs to the logged-in user
    @PreAuthorize("hasAnyAuthority('PERSONAL', 'BUSINESS') and @securityGuard.isPaymentMethodOwner(authentication, #paymentMethodId)")
    @PutMapping("/{paymentMethodId}")
    public ResponseEntity<ApiResponse<Void>> updatePaymentMethod(
            @PathVariable Long paymentMethodId,
            @Valid @RequestBody UpdatePaymentMethodRequest request) {

        paymentMethodService.updatePaymentMethod(paymentMethodId, request);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Payment method updated successfully", null)
        );
    }
}