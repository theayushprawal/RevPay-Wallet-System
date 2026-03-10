package com.revpay.controller;

import com.revpay.dto.ApiResponse;
import com.revpay.service.MoneyRequestService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoneyRequestControllerTest {

    @Mock
    private MoneyRequestService moneyRequestService;

    @InjectMocks
    private MoneyRequestController moneyRequestController;

    private Long senderId;
    private Long receiverId;
    private Long requestId;
    private BigDecimal amount;
    private String note;

    @BeforeEach
    void setUp() {
        senderId = 1L;
        receiverId = 2L;
        requestId = 10L;
        amount = new BigDecimal("500.00");
        note = "Dinner payment";
    }

    /**
     * Test Create Money Request
     */
    @Test
    void testCreateRequest() {

        doNothing().when(moneyRequestService)
                .createRequest(senderId, receiverId, amount, note);

        ResponseEntity<ApiResponse<Void>> response =
                moneyRequestController.createRequest(senderId, receiverId, amount, note);

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Money request created successfully",
                response.getBody().getMessage());

        verify(moneyRequestService, times(1))
                .createRequest(senderId, receiverId, amount, note);
    }

    /**
     * Test Accept Money Request
     */
    @Test
    void testAcceptRequest() {

        doNothing().when(moneyRequestService)
                .acceptRequest(requestId, "1234");

        ResponseEntity<ApiResponse<Void>> response =
                moneyRequestController.acceptRequest(requestId, "1234");

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Money request accepted",
                response.getBody().getMessage());

        verify(moneyRequestService, times(1))
                .acceptRequest(requestId, "1234");
    }

    /**
     * Test Decline Money Request
     */
    @Test
    void testDeclineRequest() {

        doNothing().when(moneyRequestService)
                .declineRequest(requestId);

        ResponseEntity<ApiResponse<Void>> response =
                moneyRequestController.declineRequest(requestId);

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Money request declined",
                response.getBody().getMessage());

        verify(moneyRequestService, times(1))
                .declineRequest(requestId);
    }

    /**
     * Test Cancel Money Request
     */
    @Test
    void testCancelRequest() {

        doNothing().when(moneyRequestService)
                .cancelRequest(requestId);

        ResponseEntity<ApiResponse<Void>> response =
                moneyRequestController.cancelRequest(requestId);

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Money request cancelled",
                response.getBody().getMessage());

        verify(moneyRequestService, times(1))
                .cancelRequest(requestId);
    }
}