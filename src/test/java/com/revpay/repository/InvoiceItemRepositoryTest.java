package com.revpay.repository;

import com.revpay.model.Invoice;
import com.revpay.model.InvoiceItem;
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

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class InvoiceItemRepositoryTest {

    @Autowired
    private InvoiceItemRepository invoiceItemRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByInvoice() {

        // ===== Create Business User =====
        User business = new User();
        business.setFullName("Business Owner");
        business.setEmail("business@test.com");
        business.setPhone("8888888888");
        business.setPasswordHash("password");
        business.setUserType(UserType.BUSINESS);
        business.setStatus(UserStatus.ACTIVE);
        business.setFailedAttempts(0);
        business.setIsLocked(YesNoStatus.NO);
        business.setCreatedAt(LocalDateTime.now());

        business = userRepository.save(business);

        // ===== Create Invoice =====
        Invoice invoice = new Invoice();
        invoice.setBusiness(business);
        invoice.setCustomerId(1L);
        invoice.setCustomerName("Customer One");
        invoice.setTotalAmount(BigDecimal.valueOf(1000));
        invoice.setStatus(InvoiceStatus.SENT);
        invoice.setDueDate(LocalDate.now().plusDays(7));
        invoice.setCreatedAt(LocalDateTime.now());

        invoice = invoiceRepository.save(invoice);

        // ===== Create InvoiceItem 1 =====
        InvoiceItem item1 = new InvoiceItem();
        item1.setInvoice(invoice);
        item1.setItemName("Laptop");
        item1.setQuantity(1);
        item1.setPrice(BigDecimal.valueOf(800));

        invoiceItemRepository.save(item1);

        // ===== Create InvoiceItem 2 =====
        InvoiceItem item2 = new InvoiceItem();
        item2.setInvoice(invoice);
        item2.setItemName("Mouse");
        item2.setQuantity(2);
        item2.setPrice(BigDecimal.valueOf(100));

        invoiceItemRepository.save(item2);

        // ===== Test Repository Method =====
        List<InvoiceItem> items = invoiceItemRepository.findByInvoice(invoice);

        // ===== Assertions =====
        assertNotNull(items);
        assertEquals(2, items.size());
    }
}