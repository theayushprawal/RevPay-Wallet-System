package com.revpay.service.impl;

import java.time.LocalDate;
import java.util.List;

import com.revpay.dto.UpdatePaymentMethodRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revpay.dto.AddPaymentMethodRequest;
import com.revpay.model.PaymentMethod;
import com.revpay.model.User;
import com.revpay.model.enums.PaymentMethodType;
import com.revpay.model.enums.YesNoStatus;
import com.revpay.repository.PaymentMethodRepository;
import com.revpay.repository.UserRepository;
import com.revpay.service.PaymentMethodService;

@Service
@Transactional
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;

    public PaymentMethodServiceImpl(PaymentMethodRepository paymentMethodRepository,
                                    UserRepository userRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void addPaymentMethod(AddPaymentMethodRequest request) {

        if (request == null || request.getUserId() == null) {
            throw new IllegalArgumentException("Invalid payment method request");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        PaymentMethod pm = new PaymentMethod();
        pm.setUser(user);
        pm.setMethodType(request.getMethodType());
        pm.setPaymentName(request.getPaymentName());
        pm.setBillingAddress(request.getBillingAddress());
        pm.setIsDefault(
                Boolean.TRUE.equals(request.getIsDefault())
                        ? YesNoStatus.YES
                        : YesNoStatus.NO
        );

        // CARD
        if (request.getMethodType() == PaymentMethodType.CARD) {
            validateCard(request);

            pm.setLast4(
                    request.getCardNumber()
                            .substring(request.getCardNumber().length() - 4)
            );
            pm.setCvv(request.getCvv());
            pm.setExpiryDate(request.getExpiryDate());
        }

        // BANK
        if (request.getMethodType() == PaymentMethodType.BANK) {
            validateBank(request);
            pm.setIfscCode(request.getIfscCode());
        }

        // If setting default → unset others
        if (pm.getIsDefault() == YesNoStatus.YES) {
            unsetExistingDefault(user);
        }

        paymentMethodRepository.save(pm);
    }

    @Override
    public List<PaymentMethod> getPaymentMethods(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return paymentMethodRepository.findByUser(user);
    }

    @Override
    public void setDefaultPaymentMethod(Long userId, Long paymentMethodId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        PaymentMethod pm = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new IllegalArgumentException("Payment method not found"));

        if (!pm.getUser().getUserId().equals(userId)) {
            throw new IllegalStateException("Payment method does not belong to user");
        }

        unsetExistingDefault(user);

        pm.setIsDefault(YesNoStatus.YES);
        paymentMethodRepository.save(pm);
    }

    @Override
    public void deletePaymentMethod(Long userId, Long paymentMethodId) {

        PaymentMethod pm = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new IllegalArgumentException("Payment method not found"));

        if (!pm.getUser().getUserId().equals(userId)) {
            throw new IllegalStateException("Payment method does not belong to user");
        }

        paymentMethodRepository.delete(pm); // soft delete can be added later
    }

    // ===================== HELPERS =====================

    private void unsetExistingDefault(User user) {
        List<PaymentMethod> methods = paymentMethodRepository.findByUser(user);
        for (PaymentMethod method : methods) {
            if (method.getIsDefault() == YesNoStatus.YES) {
                method.setIsDefault(YesNoStatus.NO);
                paymentMethodRepository.save(method);
            }
        }
    }

    private void validateCard(AddPaymentMethodRequest request) {
        if (request.getCardNumber() == null || request.getCardNumber().length() < 12) {
            throw new IllegalArgumentException("Invalid card number");
        }
        if (request.getCvv() == null || request.getCvv().length() != 3) {
            throw new IllegalArgumentException("Invalid CVV");
        }
        if (request.getExpiryDate() == null || request.getExpiryDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid expiry date");
        }
    }

    private void validateBank(AddPaymentMethodRequest request) {
        if (request.getIfscCode() == null) {
            throw new IllegalArgumentException("IFSC code required");
        }
        if (request.getAccountNumber() == null) {
            throw new IllegalArgumentException("Account number required");
        }
    }

    @Override
    public void updatePaymentMethod(Long paymentMethodId,
                                    UpdatePaymentMethodRequest request) {

        if (paymentMethodId == null || request == null || request.getUserId() == null) {
            throw new IllegalArgumentException("Invalid update request");
        }

        PaymentMethod pm = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new IllegalArgumentException("Payment method not found"));

        // Ownership check
        if (!pm.getUser().getUserId().equals(request.getUserId())) {
            throw new IllegalStateException("Payment method does not belong to user");
        }

        // Allowed updates
        if (request.getPaymentName() != null) {
            pm.setPaymentName(request.getPaymentName());
        }

        if (request.getBillingAddress() != null) {
            pm.setBillingAddress(request.getBillingAddress());
        }

        if (request.getExpiryDate() != null) {
            pm.setExpiryDate(request.getExpiryDate());
        }

        paymentMethodRepository.save(pm);
    }
}