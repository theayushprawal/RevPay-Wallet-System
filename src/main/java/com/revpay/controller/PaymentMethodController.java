package com.revpay.controller;

import java.util.List;

import com.revpay.dto.UpdatePaymentMethodRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revpay.dto.AddPaymentMethodRequest;
import com.revpay.model.PaymentMethod;
import com.revpay.service.PaymentMethodService;

@RestController
@RequestMapping("/payment-methods")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    public PaymentMethodController(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    @PostMapping
    public ResponseEntity<String> addPaymentMethod(
            @RequestBody AddPaymentMethodRequest request) {

        paymentMethodService.addPaymentMethod(request);
        return ResponseEntity.ok("Payment method added successfully");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<PaymentMethod>> getPaymentMethods(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                paymentMethodService.getPaymentMethods(userId)
        );
    }

    @PostMapping("/{userId}/{paymentMethodId}/default")
    public ResponseEntity<String> setDefault(
            @PathVariable Long userId,
            @PathVariable Long paymentMethodId) {

        paymentMethodService.setDefaultPaymentMethod(userId, paymentMethodId);
        return ResponseEntity.ok("Default payment method updated");
    }

    @DeleteMapping("/{userId}/{paymentMethodId}")
    public ResponseEntity<String> deletePaymentMethod(
            @PathVariable Long userId,
            @PathVariable Long paymentMethodId) {

        paymentMethodService.deletePaymentMethod(userId, paymentMethodId);
        return ResponseEntity.ok("Payment method deleted");
    }

    @PutMapping("/{paymentMethodId}")
    public ResponseEntity<String> updatePaymentMethod(
            @PathVariable Long paymentMethodId,
            @RequestBody UpdatePaymentMethodRequest request) {

        paymentMethodService.updatePaymentMethod(paymentMethodId, request);
        return ResponseEntity.ok("Payment method updated successfully");
    }
}