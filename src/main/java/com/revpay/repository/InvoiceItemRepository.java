package com.revpay.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revpay.model.Invoice;
import com.revpay.model.InvoiceItem;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {

    // Fetch all items for a given invoice
    List<InvoiceItem> findByInvoice(Invoice invoice);
}