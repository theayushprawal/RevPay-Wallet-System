package com.revpay.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.revpay.model.Invoice;
import com.revpay.model.User;
import com.revpay.model.enums.InvoiceStatus;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // OPTIMIZATION: Fetches the Invoice + its Items + the Business User in 1 single query
    @EntityGraph(attributePaths = {"items", "business"})
    List<Invoice> findByBusiness(User business);

    @EntityGraph(attributePaths = {"items", "business"})
    List<Invoice> findByCustomerId(Long customerId);

    @EntityGraph(attributePaths = {"items", "business"})
    List<Invoice> findByBusinessAndStatus(User business, InvoiceStatus status);

    @EntityGraph(attributePaths = {"items", "business"})
    List<Invoice> findByCustomerIdAndStatus(Long customerId, InvoiceStatus status);

    // OPTIMIZATION: Override the default findById so single-invoice views are also lightning fast
    @EntityGraph(attributePaths = {"items", "business"})
    Optional<Invoice> findById(Long id);

    // =================================================================================
    // AGGREGATION QUERIES
    // These only return a single number (SUM), so they don't fetch entities.
    // =================================================================================

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