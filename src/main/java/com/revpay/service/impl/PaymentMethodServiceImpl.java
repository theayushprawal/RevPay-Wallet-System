package com.revpay.service.impl;

import java.time.LocalDate;
import java.util.List;

import com.revpay.dto.UpdatePaymentMethodRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final Logger log =
            LogManager.getLogger(PaymentMethodServiceImpl.class);

    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;

    public PaymentMethodServiceImpl(PaymentMethodRepository paymentMethodRepository,
                                    UserRepository userRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void addPaymentMethod(AddPaymentMethodRequest request) {

        log.info("Adding payment method for userId={} type={}",
                request != null ? request.getUserId() : null,
                request != null ? request.getMethodType() : null);

        if (request == null || request.getUserId() == null) {
            log.warn("Add payment method failed: invalid request");
            throw new IllegalArgumentException("Invalid payment method request");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> {
                    log.warn("Add payment method failed: user not found userId={}",
                            request.getUserId());
                    return new IllegalArgumentException("User not found");
                });

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
            log.info("Setting default payment method userId={}", user.getUserId());
            unsetExistingDefault(user);
        }

        paymentMethodRepository.save(pm);

        log.info("Payment method added successfully userId={} methodType={}",
                user.getUserId(), pm.getMethodType());
    }

    @Override
    public List<PaymentMethod> getPaymentMethods(Long userId) {

        log.info("Fetching payment methods for userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Fetch payment methods failed: user not found userId={}", userId);
                    return new IllegalArgumentException("User not found");
                });

        List<PaymentMethod> methods = paymentMethodRepository.findByUser(user);

        log.info("Payment methods fetched count={} userId={}",
                methods.size(), userId);

        return methods;
    }

    @Override
    public void setDefaultPaymentMethod(Long userId, Long paymentMethodId) {

        log.info("Setting default payment method userId={} paymentMethodId={}",
                userId, paymentMethodId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Set default failed: user not found userId={}", userId);
                    return new IllegalArgumentException("User not found");
                });

        PaymentMethod pm = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> {
                    log.warn("Set default failed: payment method not found id={}", paymentMethodId);
                    return new IllegalArgumentException("Payment method not found");
                });

        if (!pm.getUser().getUserId().equals(userId)) {
            log.warn("Set default failed: ownership mismatch paymentMethodId={}", paymentMethodId);
            throw new IllegalStateException("Payment method does not belong to user");
        }

        unsetExistingDefault(user);

        pm.setIsDefault(YesNoStatus.YES);
        paymentMethodRepository.save(pm);

        log.info("Default payment method updated userId={} paymentMethodId={}",
                userId, paymentMethodId);
    }

    @Override
    public void deletePaymentMethod(Long userId, Long paymentMethodId) {

        log.info("Deleting payment method userId={} paymentMethodId={}",
                userId, paymentMethodId);

        PaymentMethod pm = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> {
                    log.warn("Delete payment method failed: not found id={}", paymentMethodId);
                    return new IllegalArgumentException("Payment method not found");
                });

        if (!pm.getUser().getUserId().equals(userId)) {
            log.warn("Delete payment method failed: ownership mismatch paymentMethodId={}",
                    paymentMethodId);
            throw new IllegalStateException("Payment method does not belong to user");
        }

        paymentMethodRepository.delete(pm); // soft delete can be added later

        log.info("Payment method deleted userId={} paymentMethodId={}",
                userId, paymentMethodId);
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

        log.info("Updating payment method paymentMethodId={} userId={}",
                paymentMethodId,
                request != null ? request.getUserId() : null);

        if (paymentMethodId == null || request == null || request.getUserId() == null) {
            log.warn("Update payment method failed: invalid request");
            throw new IllegalArgumentException("Invalid update request");
        }

        PaymentMethod pm = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> {
                    log.warn("Update payment method failed: not found id={}", paymentMethodId);
                    return new IllegalArgumentException("Payment method not found");
                });

        // Ownership check
        if (!pm.getUser().getUserId().equals(request.getUserId())) {
            log.warn("Update payment method failed: ownership mismatch paymentMethodId={}",
                    paymentMethodId);
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

        log.info("Payment method updated successfully paymentMethodId={}",
                paymentMethodId);
    }
}