package com.revpay.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // All transactions where user is sender
    List<Transaction> findBySender(User sender);

    // All transactions where user is receiver
    List<Transaction> findByReceiver(User receiver);

    // Filter by type
    List<Transaction> findBySenderAndTxnType(User sender, TransactionType txnType);

    List<Transaction> findByReceiverAndTxnType(User receiver, TransactionType txnType);

    List<Transaction> findTop5BySenderOrReceiverOrderByTxnDateDesc(User sender, User receiver);

    List<Transaction> findBySenderOrReceiver(User sender, User receiver);

    // Filter by status
    List<Transaction> findBySenderAndStatus(User sender, TransactionStatus status);

    List<Transaction> findByReceiverAndStatus(User receiver, TransactionStatus status);

    @Query("""
    SELECT t
    FROM Transaction t
    WHERE
        (t.sender.userId = :userId OR t.receiver.userId = :userId)

    AND (:txnType IS NULL OR t.txnType = :txnType)
    AND (:status IS NULL OR t.status = :status)

    AND (:startDate IS NULL OR t.txnDate >= :startDate)
    AND (:endDate IS NULL OR t.txnDate <= :endDate)

    AND (:minAmount IS NULL OR t.amount >= :minAmount)
    AND (:maxAmount IS NULL OR t.amount <= :maxAmount)

    AND (
          :searchTerm IS NULL
          OR LOWER(t.sender.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
          OR LOWER(t.receiver.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
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

}