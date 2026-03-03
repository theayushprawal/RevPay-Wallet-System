package com.revpay.service;

import java.util.List;

import com.revpay.dto.AddPaymentMethodRequest;
import com.revpay.dto.UpdatePaymentMethodRequest;
import com.revpay.model.PaymentMethod;

public interface PaymentMethodService {

    void addPaymentMethod(AddPaymentMethodRequest request);

    List<PaymentMethod> getPaymentMethods(Long userId);

    void setDefaultPaymentMethod(Long userId, Long paymentMethodId);

    void deletePaymentMethod(Long userId, Long paymentMethodId);

    void updatePaymentMethod(Long paymentMethodId, UpdatePaymentMethodRequest request);
}