package com.revpay.repository;

import com.revpay.model.Invoice;
import com.revpay.model.User;
import com.revpay.model.enums.InvoiceStatus;
import com.revpay.model.enums.UserStatus;
import com.revpay.model.enums.UserType;
import com.revpay.model.enums.YesNoStatus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class InvoiceRepositoryTest {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private UserRepository userRepository;

    // ===============================
    // Helper Methods
    // ===============================

    private User createBusinessUser() {

        User user = new User();
        user.setFullName("Test Business");
        user.setEmail(UUID.randomUUID() + "@test.com");
        user.setPhone("9999999999");
        user.setPasswordHash("password");

        user.setUserType(UserType.BUSINESS);
        user.setStatus(UserStatus.ACTIVE);
        user.setFailedAttempts(0);
        user.setIsLocked(YesNoStatus.NO);
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    private Invoice createInvoice(User business, Long customerId,
                                  InvoiceStatus status, BigDecimal amount) {

        Invoice invoice = new Invoice();
        invoice.setBusiness(business);
        invoice.setCustomerId(customerId);
        invoice.setCustomerName("Customer");

        invoice.setTotalAmount(amount);
        invoice.setStatus(status);
        invoice.setDueDate(LocalDate.now().plusDays(5));
        invoice.setCreatedAt(LocalDateTime.now());

        return invoiceRepository.save(invoice);
    }

    // ===============================
    // Tests
    // ===============================

    @Test
    public void testFindByBusiness() {

        User business = createBusinessUser();

        createInvoice(business, 1L, InvoiceStatus.SENT, BigDecimal.valueOf(500));

        List<Invoice> invoices = invoiceRepository.findByBusiness(business);

        assertEquals(1, invoices.size());
    }

    @Test
    public void testFindByCustomerId() {

        User business = createBusinessUser();

        createInvoice(business, 10L, InvoiceStatus.SENT, BigDecimal.valueOf(400));

        List<Invoice> invoices = invoiceRepository.findByCustomerId(10L);

        assertEquals(1, invoices.size());
    }

    @Test
    public void testFindByBusinessAndStatus() {

        User business = createBusinessUser();

        createInvoice(business, 1L, InvoiceStatus.PAID, BigDecimal.valueOf(300));

        List<Invoice> invoices =
                invoiceRepository.findByBusinessAndStatus(business, InvoiceStatus.PAID);

        assertEquals(1, invoices.size());
    }

    @Test
    public void testFindByCustomerIdAndStatus() {

        User business = createBusinessUser();

        createInvoice(business, 5L, InvoiceStatus.SENT, BigDecimal.valueOf(200));

        List<Invoice> invoices =
                invoiceRepository.findByCustomerIdAndStatus(5L, InvoiceStatus.SENT);

        assertEquals(1, invoices.size());
    }

    @Test
    public void testGetTotalPaid() {

        User business = createBusinessUser();

        createInvoice(business, 1L, InvoiceStatus.PAID, BigDecimal.valueOf(1000));

        BigDecimal totalPaid = invoiceRepository.getTotalPaid(business.getUserId());

        assertTrue(totalPaid.compareTo(BigDecimal.valueOf(1000)) == 0);
    }

    @Test
    public void testGetTotalPending() {

        User business = createBusinessUser();

        createInvoice(business, 1L, InvoiceStatus.SENT, BigDecimal.valueOf(500));

        BigDecimal totalPending = invoiceRepository.getTotalPending(business.getUserId());

        assertTrue(totalPending.compareTo(BigDecimal.valueOf(500)) == 0);
    }

    @Test
    public void testGetTotalOverdue() {

        User business = createBusinessUser();

        createInvoice(business, 1L, InvoiceStatus.OVERDUE, BigDecimal.valueOf(200));

        BigDecimal totalOverdue = invoiceRepository.getTotalOverdue(business.getUserId());

        assertTrue(totalOverdue.compareTo(BigDecimal.valueOf(200)) == 0);
    }
}