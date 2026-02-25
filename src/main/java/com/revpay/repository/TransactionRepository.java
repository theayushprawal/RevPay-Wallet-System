package com.revpay.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
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

    // Filter by status
    List<Transaction> findBySenderAndStatus(User sender, TransactionStatus status);

    List<Transaction> findByReceiverAndStatus(User receiver, TransactionStatus status);
}