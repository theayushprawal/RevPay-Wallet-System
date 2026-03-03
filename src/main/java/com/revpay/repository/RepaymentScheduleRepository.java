package com.revpay.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revpay.model.Loan;
import com.revpay.model.RepaymentSchedule;

public interface RepaymentScheduleRepository
        extends JpaRepository<RepaymentSchedule, Long> {

    List<RepaymentSchedule> findByLoan(Loan loan);
}