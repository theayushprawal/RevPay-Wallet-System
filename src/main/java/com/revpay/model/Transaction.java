package com.revpay.model;

import java.time.LocalDateTime;
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
    private Double amount;

    @Column(name = "TXN_TYPE")
    private String txnType;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "REMARKS")
    private String remarks;

    @Column(name = "TXN_DATE")
    private LocalDateTime txnDate;

    public Transaction() {}

    public Transaction(Long txnId, User sender, User receiver,
                       Double amount, String txnType,
                       String status, String remarks,
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

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getTxnType() { return txnType; }
    public void setTxnType(String txnType) { this.txnType = txnType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getTxnDate() { return txnDate; }
    public void setTxnDate(LocalDateTime txnDate) { this.txnDate = txnDate; }
}