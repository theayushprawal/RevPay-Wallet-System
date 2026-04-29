package com.revpay.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.revpay.dto.TopCustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.revpay.model.Transaction;
import com.revpay.model.User;
import com.revpay.model.enums.TransactionStatus;
import com.revpay.model.enums.TransactionType;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // OPTIMIZATION: Overriding findById for fast single-transaction views
    @EntityGraph(attributePaths = {"sender", "receiver"})
    Optional<Transaction> findById(Long id);

    // OPTIMIZATION: Applied EntityGraphs to all list queries to fetch users in 1 JOIN
    @EntityGraph(attributePaths = {"sender", "receiver"})
    List<Transaction> findBySender(User sender);

    @EntityGraph(attributePaths = {"sender", "receiver"})
    List<Transaction> findByReceiver(User receiver);

    @EntityGraph(attributePaths = {"sender", "receiver"})
    List<Transaction> findBySenderAndTxnType(User sender, TransactionType txnType);

    @EntityGraph(attributePaths = {"sender", "receiver"})
    List<Transaction> findByReceiverAndTxnType(User receiver, TransactionType txnType);

    @EntityGraph(attributePaths = {"sender", "receiver"})
    List<Transaction> findTop5BySenderOrReceiverOrderByTxnDateDesc(User sender, User receiver);

    @EntityGraph(attributePaths = {"sender", "receiver"})
    List<Transaction> findBySenderOrReceiver(User sender, User receiver);

    @EntityGraph(attributePaths = {"sender", "receiver"})
    List<Transaction> findBySenderAndStatus(User sender, TransactionStatus status);

    @EntityGraph(attributePaths = {"sender", "receiver"})
    List<Transaction> findByReceiverAndStatus(User receiver, TransactionStatus status);

    // OPTIMIZATION: Added EntityGraph. (A standard LEFT JOIN in JPQL doesn't fetch the data
    // into the object unless you use 'FETCH', but EntityGraph handles it perfectly with Pagination!)
    @EntityGraph(attributePaths = {"sender", "receiver"})
    @Query("""
    SELECT t
    FROM Transaction t
    LEFT JOIN t.sender s
    LEFT JOIN t.receiver r
    WHERE
        (s.userId = :userId OR r.userId = :userId)

    AND (:txnType IS NULL OR t.txnType = :txnType)
    AND (:status IS NULL OR t.status = :status)

    AND (:startDate IS NULL OR t.txnDate >= :startDate)
    AND (:endDate IS NULL OR t.txnDate <= :endDate)

    AND (:minAmount IS NULL OR t.amount >= :minAmount)
    AND (:maxAmount IS NULL OR t.amount <= :maxAmount)

    AND (
          :searchTerm IS NULL
          OR LOWER(s.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
          OR LOWER(r.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
          OR CAST(t.txnId AS string) LIKE CONCAT('%', :searchTerm, '%')
    )""")
    Page<Transaction> filterTransactionsPaged(
            @Param("userId") Long userId,
            @Param("txnType") TransactionType txnType,
            @Param("status") TransactionStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );


    // =================================================================================
    // AGGREGATION & DTO QUERIES
    // These only return numbers or DTOs. Zero risk of N+1.
    // =================================================================================

    @Query("""
    SELECT COALESCE(SUM(t.amount),0)
    FROM Transaction t
    WHERE t.sender.userId = :userId
    AND t.status = 'SUCCESS'
    """)
    BigDecimal getTotalSent(@Param("userId") Long userId);

    @Query("""
    SELECT COALESCE(SUM(t.amount),0)
    FROM Transaction t
    WHERE t.receiver.userId = :userId
    AND t.status = 'SUCCESS'
    """)
    BigDecimal getTotalReceived(@Param("userId") Long userId);

    // for top customer analytics
    @Query("""
    SELECT new com.revpay.dto.TopCustomerResponse(
    t.sender.userId,
    t.sender.fullName,
    COUNT(t),
    SUM(t.amount)
    )
    FROM Transaction t
    WHERE t.receiver.userId = :businessId
    AND t.status = 'SUCCESS'
    GROUP BY t.sender.userId, t.sender.fullName
    ORDER BY SUM(t.amount) DESC
    """)
    List<TopCustomerResponse> getTopCustomers(@Param("businessId") Long businessId);

    // For fetching revenue report (daily/weekly/monthly)
    @Query("""
    SELECT COALESCE(SUM(t.amount),0)
    FROM Transaction t
    WHERE t.receiver.userId = :businessId
    AND t.status = 'SUCCESS'
    AND t.txnDate >= :startDate
    """)
    BigDecimal getRevenueFromDate(
            @Param("businessId") Long businessId,
            @Param("startDate") LocalDateTime startDate
    );

}