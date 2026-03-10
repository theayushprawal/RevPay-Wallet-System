package com.revpay.service.impl;

import com.revpay.dto.AddPaymentMethodRequest;
import com.revpay.dto.UpdatePaymentMethodRequest;
import com.revpay.model.PaymentMethod;
import com.revpay.model.User;
import com.revpay.model.enums.PaymentMethodType;
import com.revpay.model.enums.YesNoStatus;
import com.revpay.repository.PaymentMethodRepository;
import com.revpay.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentMethodServiceImplTest {

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PaymentMethodServiceImpl paymentMethodService;

    private User user;
    private PaymentMethod paymentMethod;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUserId(1L);

        paymentMethod = new PaymentMethod();
        paymentMethod.setPmId(100L);   // correct setter from entity
        paymentMethod.setUser(user);
        paymentMethod.setPaymentName("Visa Card");
        paymentMethod.setIsDefault(YesNoStatus.NO);
    }

    // ADD PAYMENT METHOD (CARD)
    @Test
    void testAddPaymentMethodCardSuccess() {

        AddPaymentMethodRequest request = new AddPaymentMethodRequest();
        request.setUserId(1L);
        request.setMethodType(PaymentMethodType.CARD);
        request.setCardNumber("1234567890123456");
        request.setCvv("123");
        request.setExpiryDate(LocalDate.now().plusYears(1));
        request.setPaymentName("Visa Card");
        request.setBillingAddress("Bangalore");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        paymentMethodService.addPaymentMethod(request);

        verify(paymentMethodRepository, times(1))
                .save(any(PaymentMethod.class));
    }

    // USER NOT FOUND
    @Test
    void testAddPaymentMethodUserNotFound() {

        AddPaymentMethodRequest request = new AddPaymentMethodRequest();
        request.setUserId(5L);

        when(userRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> paymentMethodService.addPaymentMethod(request));
    }

    // GET PAYMENT METHODS
    @Test
    void testGetPaymentMethodsSuccess() {

        List<PaymentMethod> list = new ArrayList<>();
        list.add(paymentMethod);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentMethodRepository.findByUser(user)).thenReturn(list);

        List<PaymentMethod> result =
                paymentMethodService.getPaymentMethods(1L);

        assertEquals(1, result.size());

        verify(paymentMethodRepository).findByUser(user);
    }

    // SET DEFAULT PAYMENT METHOD
    @Test
    void testSetDefaultPaymentMethodSuccess() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentMethodRepository.findById(100L))
                .thenReturn(Optional.of(paymentMethod));
        when(paymentMethodRepository.findByUser(user))
                .thenReturn(new ArrayList<>());

        paymentMethodService.setDefaultPaymentMethod(1L, 100L);

        verify(paymentMethodRepository).save(paymentMethod);

        assertEquals(YesNoStatus.YES, paymentMethod.getIsDefault());
    }

    // DELETE PAYMENT METHOD
    @Test
    void testDeletePaymentMethodSuccess() {

        when(paymentMethodRepository.findById(100L))
                .thenReturn(Optional.of(paymentMethod));

        paymentMethodService.deletePaymentMethod(1L, 100L);

        verify(paymentMethodRepository).delete(paymentMethod);
    }

    // DELETE OWNERSHIP MISMATCH
    @Test
    void testDeletePaymentMethodOwnershipMismatch() {

        User otherUser = new User();
        otherUser.setUserId(2L);

        paymentMethod.setUser(otherUser);

        when(paymentMethodRepository.findById(100L))
                .thenReturn(Optional.of(paymentMethod));

        assertThrows(IllegalStateException.class,
                () -> paymentMethodService.deletePaymentMethod(1L, 100L));
    }

    // UPDATE PAYMENT METHOD
    @Test
    void testUpdatePaymentMethodSuccess() {

        UpdatePaymentMethodRequest request =
                new UpdatePaymentMethodRequest();

        request.setUserId(1L);
        request.setPaymentName("Updated Card");
        request.setBillingAddress("New Address");
        request.setExpiryDate(LocalDate.now().plusYears(2));

        when(paymentMethodRepository.findById(100L))
                .thenReturn(Optional.of(paymentMethod));

        paymentMethodService.updatePaymentMethod(100L, request);

        verify(paymentMethodRepository).save(paymentMethod);

        assertEquals("Updated Card", paymentMethod.getPaymentName());
    }

    // UPDATE OWNERSHIP ERROR
    @Test
    void testUpdatePaymentMethodOwnershipMismatch() {

        User otherUser = new User();
        otherUser.setUserId(2L);

        paymentMethod.setUser(otherUser);

        UpdatePaymentMethodRequest request =
                new UpdatePaymentMethodRequest();

        request.setUserId(1L);

        when(paymentMethodRepository.findById(100L))
                .thenReturn(Optional.of(paymentMethod));

        assertThrows(IllegalStateException.class,
                () -> paymentMethodService.updatePaymentMethod(100L, request));
    }
}

