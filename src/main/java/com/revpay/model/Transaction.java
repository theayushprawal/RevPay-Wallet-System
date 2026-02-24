package com.revpay.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.revpay.model.enums.TransactionStatus;
import com.revpay.model.enums.TransactionType;
import jakarta.persistence.*;

@Entity
@Table(name = "TRANSACTIONS")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "txn_seq")
    @SequenceGenerator(name = "txn_seq", sequenceName = "GEN_TXN_ID", allocationSize = 1)
    @Column(name = "TXN_ID")
    private Long txnId;

    @ManyToOne
    @JoinColumn(name = "SENDER_ID")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "RECEIVER_ID")
    private User receiver;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "TXN_TYPE")
    private TransactionType txnType;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private TransactionStatus status;

    @Column(name = "REMARKS")
    private String remarks;

    @Column(name = "TXN_DATE")
    private LocalDateTime txnDate;

    public Transaction() {}

    public Transaction(Long txnId, User sender, User receiver,
                       BigDecimal amount, TransactionType txnType,
                       TransactionStatus status, String remarks,
                       LocalDateTime txnDate) {
        this.txnId = txnId;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.txnType = txnType;
        this.status = status;
        this.remarks = remarks;
        this.txnDate = txnDate;
    }

    public Long getTxnId() { return txnId; }
    public void setTxnId(Long txnId) { this.txnId = txnId; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public User getReceiver() { return receiver; }
    public void setReceiver(User receiver) { this.receiver = receiver; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public TransactionType getTxnType() { return txnType; }
    public void setTxnType(TransactionType txnType) { this.txnType = txnType; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getTxnDate() { return txnDate; }
    public void setTxnDate(LocalDateTime txnDate) { this.txnDate = txnDate; }
}