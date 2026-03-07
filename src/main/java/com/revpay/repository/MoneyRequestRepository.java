package com.revpay.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.revpay.model.MoneyRequest;
import com.revpay.model.User;
import com.revpay.model.enums.RequestStatus;

@Repository
public interface MoneyRequestRepository extends JpaRepository<MoneyRequest, Long> {

    // Incoming requests for a user
    List<MoneyRequest> findByReceiver(User receiver);

    // Outgoing requests by a user
    List<MoneyRequest> findBySender(User sender);

    // Incoming requests filtered by status (PENDING, ACCEPTED, etc.)
    List<MoneyRequest> findByReceiverAndStatus(User receiver, RequestStatus status);

    // Outgoing requests filtered by status
    List<MoneyRequest> findBySenderAndStatus(User sender, RequestStatus status);

    @Query("""
    SELECT COALESCE(SUM(m.amount),0)
    FROM MoneyRequest m
    WHERE m.receiver.userId = :userId
    AND m.status = 'PENDING'
    """)
    BigDecimal getPendingRequestAmount(@Param("userId") Long userId);

}