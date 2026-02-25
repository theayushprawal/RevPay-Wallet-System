package com.revpay.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revpay.model.Invoice;
import com.revpay.model.User;
import com.revpay.model.enums.InvoiceStatus;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // All invoices created by a business user
    List<Invoice> findByBusiness(User business);

    // Filter invoices by status (PAID, DRAFT, OVERDUE, etc.)
    List<Invoice> findByBusinessAndStatus(User business, InvoiceStatus status);
}