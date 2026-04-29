package com.revpay.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.revpay.model.enums.LoanStatus;
import com.revpay.model.enums.YesNoStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "LOANS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loan_seq")
    @SequenceGenerator(name = "loan_seq", sequenceName = "GEN_LOAN_ID", allocationSize = 1)
    @Column(name = "LOAN_ID")
    private Long loanId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY) // OPTIMIZATION: Changed from EAGER (default) to LAZY
    @JoinColumn(name = "BUSINESS_ID")
    private User business;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "EMI")
    private BigDecimal emi;

    @Column(name = "INTEREST_RATE")
    private BigDecimal interestRate;

    @Column(name = "TENURE_MONTHS")
    private Integer tenureMonths;

    @Column(name = "PURPOSE")
    private String purpose;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private LoanStatus status;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    // @OneToMany is LAZY by default, but explicitly writing it is best practice.
    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RepaymentSchedule> repayments;

    // NEW FIELD (Document simulation)
    @Column(name = "DOCUMENT_NAME")
    private String documentName;

    @Enumerated(EnumType.STRING)
    @Column(name = "DOCUMENT_UPLOADED")
    private YesNoStatus documentUploaded;
}