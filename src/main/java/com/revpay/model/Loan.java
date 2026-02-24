package com.revpay.model;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "LOANS")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loan_seq")
    @SequenceGenerator(name = "loan_seq", sequenceName = "GEN_LOAN_ID", allocationSize = 1)
    @Column(name = "LOAN_ID")
    private Long loanId;

    @ManyToOne
    @JoinColumn(name = "BUSINESS_ID")
    private User business;

    @Column(name = "AMOUNT")
    private Double amount;

    @Column(name = "EMI")
    private Double emi;

    @Column(name = "INTEREST_RATE")
    private Double interestRate;

    @Column(name = "TENURE_MONTHS")
    private Integer tenureMonths;

    @Column(name = "PURPOSE")
    private String purpose;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL)
    private List<RepaymentSchedule> repayments;

    public Loan() {}

    public Loan(Long loanId, User business, Double amount,
                Double emi, Double interestRate,
                Integer tenureMonths, String purpose,
                String status, LocalDateTime createdAt) {
        this.loanId = loanId;
        this.business = business;
        this.amount = amount;
        this.emi = emi;
        this.interestRate = interestRate;
        this.tenureMonths = tenureMonths;
        this.purpose = purpose;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getLoanId() { return loanId; }
    public void setLoanId(Long loanId) { this.loanId = loanId; }

    public User getBusiness() { return business; }
    public void setBusiness(User business) { this.business = business; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Double getEmi() { return emi; }
    public void setEmi(Double emi) { this.emi = emi; }

    public Double getInterestRate() { return interestRate; }
    public void setInterestRate(Double interestRate) { this.interestRate = interestRate; }

    public Integer getTenureMonths() { return tenureMonths; }
    public void setTenureMonths(Integer tenureMonths) { this.tenureMonths = tenureMonths; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<RepaymentSchedule> getRepayments() { return repayments; }
    public void setRepayments(List<RepaymentSchedule> repayments) { this.repayments = repayments; }
}