package com.revpay.service;

import com.revpay.dto.ApplyLoanRequest;
import com.revpay.dto.RepayLoanRequest;
import com.revpay.model.Loan;

public interface LoanService {

    Loan applyLoan(ApplyLoanRequest request);

    void disburseLoan(Long loanId);

    void repayLoan(RepayLoanRequest request);
}