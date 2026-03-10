package com.revpay.controller;

import com.revpay.dto.ApiResponse;
import com.revpay.dto.CreateInvoiceRequest;
import com.revpay.dto.InvoiceSummaryResponse;
import com.revpay.model.Invoice;
import com.revpay.service.InvoiceService;

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
class InvoiceControllerTest {

    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private InvoiceController invoiceController;

    private Invoice invoice;
    private CreateInvoiceRequest request;

    @BeforeEach
    void setup() {
        invoice = new Invoice();

        request = new CreateInvoiceRequest();
        request.setBusinessId(1L);
    }

    /**
     * Test Create Invoice
     */
    @Test
    void testCreateInvoice() {

        when(invoiceService.createInvoice(request)).thenReturn(invoice);

        ResponseEntity<ApiResponse<Invoice>> response =
                invoiceController.createInvoice(request);

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Invoice created successfully", response.getBody().getMessage());
        assertEquals(invoice, response.getBody().getData());

        verify(invoiceService, times(1)).createInvoice(request);
    }

    /**
     * Test Send Invoice
     */
    @Test
    void testSendInvoice() {

        doNothing().when(invoiceService).sendInvoice(1L);

        ResponseEntity<ApiResponse<Void>> response =
                invoiceController.sendInvoice(1L);

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Invoice sent successfully", response.getBody().getMessage());

        verify(invoiceService, times(1)).sendInvoice(1L);
    }

    /**
     * Test Pay Invoice
     */
    @Test
    void testPayInvoice() {

        doNothing().when(invoiceService).payInvoice(1L, 2L, "1234");

        ResponseEntity<ApiResponse<Void>> response =
                invoiceController.payInvoice(1L, 2L, "1234");

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Invoice paid successfully", response.getBody().getMessage());

        verify(invoiceService, times(1)).payInvoice(1L, 2L, "1234");
    }

    /**
     * Test Cancel Invoice
     */
    @Test
    void testCancelInvoice() {

        doNothing().when(invoiceService).cancelInvoice(1L);

        ResponseEntity<ApiResponse<Void>> response =
                invoiceController.cancelInvoice(1L);

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals("Invoice cancelled successfully", response.getBody().getMessage());

        verify(invoiceService, times(1)).cancelInvoice(1L);
    }

    /**
     * Test Get Invoices For Business
     */
    @Test
    void testGetInvoicesForBusiness() {

        when(invoiceService.getInvoicesForBusiness(1L))
                .thenReturn(List.of(invoice));

        ResponseEntity<ApiResponse<List<Invoice>>> response =
                invoiceController.getInvoicesForBusiness(1L);

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());

        verify(invoiceService, times(1)).getInvoicesForBusiness(1L);
    }

    /**
     * Test Get Invoices For Customer
     */
    @Test
    void testGetInvoicesForCustomer() {

        when(invoiceService.getInvoicesForCustomer(2L))
                .thenReturn(List.of(invoice));

        ResponseEntity<ApiResponse<List<Invoice>>> response =
                invoiceController.getInvoicesForCustomer(2L);

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());

        verify(invoiceService, times(1)).getInvoicesForCustomer(2L);
    }

    /**
     * Test Invoice Summary
     */
    @Test
    void testGetInvoiceSummary() {

        InvoiceSummaryResponse summary = new InvoiceSummaryResponse();

        when(invoiceService.getInvoiceSummary(1L))
                .thenReturn(summary);

        ResponseEntity<ApiResponse<InvoiceSummaryResponse>> response =
                invoiceController.getInvoiceSummary(1L);

        assertNotNull(response);
        assertTrue(response.getBody().isSuccess());
        assertEquals(summary, response.getBody().getData());

        verify(invoiceService, times(1)).getInvoiceSummary(1L);
    }
}