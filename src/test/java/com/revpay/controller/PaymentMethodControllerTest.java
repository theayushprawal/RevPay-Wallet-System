package com.revpay.controller;

import com.revpay.dto.ApiResponse;
import com.revpay.dto.AddPaymentMethodRequest;
import com.revpay.dto.UpdatePaymentMethodRequest;
import com.revpay.model.PaymentMethod;
import com.revpay.service.PaymentMethodService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentMethodControllerTest {

    @Mock
    private PaymentMethodService paymentMethodService;

    @InjectMocks
    private PaymentMethodController paymentMethodController;

    private AddPaymentMethodRequest addRequest;
    private UpdatePaymentMethodRequest updateRequest;
    private PaymentMethod paymentMethod;

    @BeforeEach
    void setUp() {
        addRequest = new AddPaymentMethodRequest();
        updateRequest = new UpdatePaymentMethodRequest();
        paymentMethod = new PaymentMethod();
    }

    /**
     * Test Add Payment Method
     */
    @Test
    void testAddPaymentMethod() {

        doNothing().when(paymentMethodService).addPaymentMethod(addRequest);

        ResponseEntity<ApiResponse<Void>> response =
                paymentMethodController.addPaymentMethod(addRequest);

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Payment method added successfully",
                response.getBody().getMessage());

        verify(paymentMethodService, times(1)).addPaymentMethod(addRequest);
    }

    /**
     * Test Get Payment Methods
     */
    @Test
    void testGetPaymentMethods() {

        when(paymentMethodService.getPaymentMethods(1L))
                .thenReturn(List.of(paymentMethod));

        ResponseEntity<ApiResponse<List<PaymentMethod>>> response =
                paymentMethodController.getPaymentMethods(1L);

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Payment methods fetched successfully",
                response.getBody().getMessage());
        assertEquals(1, response.getBody().getData().size());

        verify(paymentMethodService, times(1)).getPaymentMethods(1L);
    }

    /**
     * Test Set Default Payment Method
     */
    @Test
    void testSetDefaultPaymentMethod() {

        doNothing().when(paymentMethodService)
                .setDefaultPaymentMethod(1L, 10L);

        ResponseEntity<ApiResponse<Void>> response =
                paymentMethodController.setDefault(1L, 10L);

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Default payment method updated",
                response.getBody().getMessage());

        verify(paymentMethodService, times(1))
                .setDefaultPaymentMethod(1L, 10L);
    }

    /**
     * Test Delete Payment Method
     */
    @Test
    void testDeletePaymentMethod() {

        doNothing().when(paymentMethodService)
                .deletePaymentMethod(1L, 10L);

        ResponseEntity<ApiResponse<Void>> response =
                paymentMethodController.deletePaymentMethod(1L, 10L);

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Payment method deleted",
                response.getBody().getMessage());

        verify(paymentMethodService, times(1))
                .deletePaymentMethod(1L, 10L);
    }

    /**
     * Test Update Payment Method
     */
    @Test
    void testUpdatePaymentMethod() {

        doNothing().when(paymentMethodService)
                .updatePaymentMethod(10L, updateRequest);

        ResponseEntity<ApiResponse<Void>> response =
                paymentMethodController.updatePaymentMethod(10L, updateRequest);

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Payment method updated successfully",
                response.getBody().getMessage());

        verify(paymentMethodService, times(1))
                .updatePaymentMethod(10L, updateRequest);
    }
}