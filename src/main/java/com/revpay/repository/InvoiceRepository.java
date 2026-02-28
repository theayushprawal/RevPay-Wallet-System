package com.revpay.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
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
}