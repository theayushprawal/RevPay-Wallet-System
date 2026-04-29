package com.revpay.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "REPAYMENT_SCHEDULE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RepaymentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "repay_seq")
    @SequenceGenerator(name = "repay_seq", sequenceName = "GEN_REPAY_ID", allocationSize = 1)
    @Column(name = "REPAYMENT_ID")
    private Long repaymentId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY) // OPTIMIZATION: Changed from EAGER (default) to LAZY
    @JoinColumn(name = "LOAN_ID")
    private Loan loan;

    @Column(name = "BALANCE_PAYMENT")
    private BigDecimal balancePayment;

    @Column(name = "EMI")
    private BigDecimal emi;

    @Column(name = "PAYMENT_DATE")
    private LocalDateTime paymentDate;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "REMARKS")
    private String remarks;
}