package com.revpay.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.revpay.model.Invoice;
import com.revpay.model.User;
import com.revpay.model.enums.InvoiceStatus;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // All invoices created by a business
    List<Invoice> findByBusiness(User business);

    // All invoices for a customer
    List<Invoice> findByCustomerId(Long customerId);

    // Filter invoices by status for a business
    List<Invoice> findByBusinessAndStatus(User business, InvoiceStatus status);

    // Filter invoices by status for a customer
    List<Invoice> findByCustomerIdAndStatus(Long customerId, InvoiceStatus status);

    //For outstanding invoice summary
    @Query("""
SELECT COALESCE(SUM(i.totalAmount),0)
FROM Invoice i
WHERE i.business.userId = :businessId
AND i.status = 'PAID'
""")
    BigDecimal getTotalPaid(@Param("businessId") Long businessId);


    @Query("""
SELECT COALESCE(SUM(i.totalAmount),0)
FROM Invoice i
WHERE i.business.userId = :businessId
AND i.status = 'SENT'
""")
    BigDecimal getTotalPending(@Param("businessId") Long businessId);


    @Query("""
SELECT COALESCE(SUM(i.totalAmount),0)
FROM Invoice i
WHERE i.business.userId = :businessId
AND i.status = 'OVERDUE'
""")
    BigDecimal getTotalOverdue(@Param("businessId") Long businessId);
}