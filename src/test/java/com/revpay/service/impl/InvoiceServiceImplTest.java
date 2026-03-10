package com.revpay.service.impl;

import com.revpay.dto.CreateInvoiceRequest;
import com.revpay.dto.InvoiceItemRequest;
import com.revpay.model.Invoice;
import com.revpay.model.User;
import com.revpay.model.enums.InvoiceStatus;
import com.revpay.model.enums.UserType;
import com.revpay.repository.InvoiceItemRepository;
import com.revpay.repository.InvoiceRepository;
import com.revpay.repository.UserRepository;
import com.revpay.service.AuthService;
import com.revpay.service.NotificationService;
import com.revpay.service.TransactionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class InvoiceServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private InvoiceItemRepository invoiceItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    private User business;
    private User customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        business = new User();
        business.setUserId(1L);
        business.setUserType(UserType.BUSINESS);

        customer = new User();
        customer.setUserId(2L);
    }

    // ----------------------------
    // CREATE INVOICE TEST
    // ----------------------------

    @Test
    void testCreateInvoiceSuccess() {

        CreateInvoiceRequest request = new CreateInvoiceRequest();
        request.setBusinessId(1L);
        request.setCustomerId(2L);
        request.setCustomerName("Test Customer");
        request.setDueDate(LocalDate.now().plusDays(10));

        InvoiceItemRequest item = new InvoiceItemRequest();
        item.setItemName("Laptop");
        item.setQuantity(2);
        item.setPrice(BigDecimal.valueOf(500));

        request.setItems(List.of(item));

        when(userRepository.findById(1L)).thenReturn(Optional.of(business));
        when(userRepository.findById(2L)).thenReturn(Optional.of(customer));
        when(invoiceRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Invoice invoice = invoiceService.createInvoice(request);

        assertNotNull(invoice);
        verify(invoiceRepository).save(any());
        verify(invoiceItemRepository).save(any());
    }

    @Test
    void testCreateInvoiceBusinessNotFound() {

        CreateInvoiceRequest request = new CreateInvoiceRequest();
        request.setBusinessId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> invoiceService.createInvoice(request));
    }

    // ----------------------------
    // SEND INVOICE TEST
    // ----------------------------

    @Test
    void testSendInvoiceSuccess() {

        Invoice invoice = new Invoice();
        invoice.setInvoiceId(10L);
        invoice.setBusiness(business);
        invoice.setCustomerId(2L);
        invoice.setStatus(InvoiceStatus.DRAFT);

        when(invoiceRepository.findById(10L)).thenReturn(Optional.of(invoice));

        invoiceService.sendInvoice(10L);

        assertEquals(InvoiceStatus.SENT, invoice.getStatus());

        verify(invoiceRepository).save(invoice);

        verify(notificationService).sendNotification(
                nullable(Long.class),
                anyString(),
                any()
        );
    }

    @Test
    void testSendInvoiceInvalidStatus() {

        Invoice invoice = new Invoice();
        invoice.setInvoiceId(10L);
        invoice.setBusiness(business);
        invoice.setStatus(InvoiceStatus.PAID);

        when(invoiceRepository.findById(10L)).thenReturn(Optional.of(invoice));

        assertThrows(IllegalStateException.class,
                () -> invoiceService.sendInvoice(10L));
    }

    // ----------------------------
    // PAY INVOICE TEST
    // ----------------------------

    @Test
    void testPayInvoiceSuccess() {

        Invoice invoice = new Invoice();
        invoice.setInvoiceId(20L);
        invoice.setBusiness(business);
        invoice.setCustomerId(2L);
        invoice.setTotalAmount(BigDecimal.valueOf(1000));
        invoice.setStatus(InvoiceStatus.SENT);

        when(invoiceRepository.findById(20L)).thenReturn(Optional.of(invoice));
        when(userRepository.findById(2L)).thenReturn(Optional.of(customer));
        when(authService.verifyTransactionPin(customer, "1234")).thenReturn(true);

        invoiceService.payInvoice(20L, 2L, "1234");

        assertEquals(InvoiceStatus.PAID, invoice.getStatus());

        verify(transactionService).sendMoney(
                anyLong(),
                anyLong(),
                any(),
                anyString(),
                anyString()
        );

        verify(notificationService).sendNotification(
                nullable(Long.class),
                anyString(),
                any()
        );
    }

    @Test
    void testPayInvoiceInvalidPin() {

        Invoice invoice = new Invoice();
        invoice.setInvoiceId(20L);
        invoice.setBusiness(business);
        invoice.setCustomerId(2L);
        invoice.setTotalAmount(BigDecimal.valueOf(1000));
        invoice.setStatus(InvoiceStatus.SENT);

        when(invoiceRepository.findById(20L)).thenReturn(Optional.of(invoice));
        when(userRepository.findById(2L)).thenReturn(Optional.of(customer));
        when(authService.verifyTransactionPin(customer, "1111")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> invoiceService.payInvoice(20L, 2L, "1111"));
    }

    // ----------------------------
    // CANCEL INVOICE TEST
    // ----------------------------

    @Test
    void testCancelInvoiceSuccess() {

        Invoice invoice = new Invoice();
        invoice.setInvoiceId(5L);
        invoice.setBusiness(business);
        invoice.setStatus(InvoiceStatus.DRAFT);

        when(invoiceRepository.findById(5L)).thenReturn(Optional.of(invoice));

        invoiceService.cancelInvoice(5L);

        assertEquals(InvoiceStatus.CANCELLED, invoice.getStatus());
        verify(invoiceRepository).save(invoice);
    }

    @Test
    void testCancelInvoiceInvalidState() {

        Invoice invoice = new Invoice();
        invoice.setInvoiceId(5L);
        invoice.setBusiness(business);
        invoice.setStatus(InvoiceStatus.PAID);

        when(invoiceRepository.findById(5L)).thenReturn(Optional.of(invoice));

        assertThrows(IllegalStateException.class,
                () -> invoiceService.cancelInvoice(5L));
    }

    // ----------------------------
    // GET BUSINESS INVOICES
    // ----------------------------

    @Test
    void testGetInvoicesForBusiness() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(business));
        when(invoiceRepository.findByBusiness(business))
                .thenReturn(List.of(new Invoice()));

        List<Invoice> invoices = invoiceService.getInvoicesForBusiness(1L);

        assertEquals(1, invoices.size());
    }

    // ----------------------------
    // GET CUSTOMER INVOICES
    // ----------------------------

    @Test
    void testGetInvoicesForCustomer() {

        when(userRepository.findById(2L)).thenReturn(Optional.of(customer));
        when(invoiceRepository.findByCustomerId(2L))
                .thenReturn(List.of(new Invoice()));

        List<Invoice> invoices = invoiceService.getInvoicesForCustomer(2L);

        assertEquals(1, invoices.size());
    }
}
