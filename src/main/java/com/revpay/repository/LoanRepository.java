package com.revpay.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revpay.model.Loan;
import com.revpay.model.User;
import com.revpay.model.enums.LoanStatus;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    // All loans for a business user
    List<Loan> findByBusiness(User business);

    // Loans filtered by status (PENDING, APPROVED, REJECTED, CLOSED)
    List<Loan> findByBusinessAndStatus(User business, LoanStatus status);
}