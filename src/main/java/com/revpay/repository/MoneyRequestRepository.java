package com.revpay.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.revpay.model.MoneyRequest;
import com.revpay.model.User;
import com.revpay.model.enums.RequestStatus;

@Repository
public interface MoneyRequestRepository extends JpaRepository<MoneyRequest, Long> {

    // OPTIMIZATION: Fetches the MoneyRequest + Sender + Receiver in 1 single query
    @EntityGraph(attributePaths = {"sender", "receiver"})
    List<MoneyRequest> findByReceiver(User receiver);

    @EntityGraph(attributePaths = {"sender", "receiver"})
    List<MoneyRequest> findBySender(User sender);

    @EntityGraph(attributePaths = {"sender", "receiver"})
    List<MoneyRequest> findByReceiverAndStatus(User receiver, RequestStatus status);

    @EntityGraph(attributePaths = {"sender", "receiver"})
    List<MoneyRequest> findBySenderAndStatus(User sender, RequestStatus status);

    // OPTIMIZATION: Override the default findById so single-request views are also lightning fast
    @EntityGraph(attributePaths = {"sender", "receiver"})
    Optional<MoneyRequest> findById(Long id);

    // =================================================================================
    // AGGREGATION QUERIES
    // Returns a single number, so no entities are fetched.
    // =================================================================================

    @Query("""
    SELECT COALESCE(SUM(m.amount),0)
    FROM MoneyRequest m
    WHERE m.receiver.userId = :userId
    AND m.status = 'PENDING'
    """)
    BigDecimal getPendingRequestAmount(@Param("userId") Long userId);

}