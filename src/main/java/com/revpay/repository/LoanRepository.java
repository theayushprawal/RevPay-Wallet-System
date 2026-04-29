package com.revpay.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revpay.model.Loan;
import com.revpay.model.User;
import com.revpay.model.enums.LoanStatus;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    // OPTIMIZATION: Overriding findById to fetch repayments and business info at once
    @EntityGraph(attributePaths = {"business", "repayments"})
    Optional<Loan> findById(Long id);

    // OPTIMIZATION: Single query fetch for the Loan list
    @EntityGraph(attributePaths = {"business"})
    List<Loan> findByBusiness(User business);

    @EntityGraph(attributePaths = {"business"})
    List<Loan> findByBusiness_UserId(Long userId);

    @EntityGraph(attributePaths = {"business"})
    List<Loan> findByBusinessAndStatus(User business, LoanStatus status);
}