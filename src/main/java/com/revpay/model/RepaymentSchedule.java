package com.revpay.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "REPAYMENT_SCHEDULE")
public class RepaymentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "repay_seq")
    @SequenceGenerator(name = "repay_seq", sequenceName = "GEN_REPAY_ID", allocationSize = 1)
    @Column(name = "REPAYMENT_ID")
    private Long repaymentId;

    @ManyToOne
    @JoinColumn(name = "LOAN_ID")
    private Loan loan;

    @Column(name = "BALANCE_PAYMENT")
    private Double balancePayment;

    @Column(name = "EMI")
    private Double emi;

    @Column(name = "PAYMENT_DATE")
    private LocalDateTime paymentDate;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "REMARKS")
    private String remarks;

    public RepaymentSchedule() {}

    public RepaymentSchedule(Long repaymentId, Loan loan,
                             Double balancePayment, Double emi,
                             LocalDateTime paymentDate,
                             String status, String remarks) {
        this.repaymentId = repaymentId;
        this.loan = loan;
        this.balancePayment = balancePayment;
        this.emi = emi;
        this.paymentDate = paymentDate;
        this.status = status;
        this.remarks = remarks;
    }

    public Long getRepaymentId() { return repaymentId; }
    public void setRepaymentId(Long repaymentId) { this.repaymentId = repaymentId; }

    public Loan getLoan() { return loan; }
    public void setLoan(Loan loan) { this.loan = loan; }

    public Double getBalancePayment() { return balancePayment; }
    public void setBalancePayment(Double balancePayment) { this.balancePayment = balancePayment; }

    public Double getEmi() { return emi; }
    public void setEmi(Double emi) { this.emi = emi; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}