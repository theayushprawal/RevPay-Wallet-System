package com.revpay.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "MONEY_REQUESTS")
public class MoneyRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mr_seq")
    @SequenceGenerator(name = "mr_seq", sequenceName = "GEN_REQ_ID", allocationSize = 1)
    @Column(name = "REQUEST_ID")
    private Long requestId;

    @ManyToOne
    @JoinColumn(name = "SENDER_ID")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "RECEIVER_ID")
    private User receiver;

    @Column(name = "AMOUNT")
    private Double amount;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "EXPIRY_DATE")
    private LocalDateTime expiryDate;

    @Column(name = "REJECTION_REASON")
    private String rejectionReason;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    public MoneyRequest() {}

    public MoneyRequest(Long requestId, User sender, User receiver,
                        Double amount, String status,
                        LocalDateTime expiryDate,
                        String rejectionReason,
                        LocalDateTime createdAt) {
        this.requestId = requestId;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.status = status;
        this.expiryDate = expiryDate;
        this.rejectionReason = rejectionReason;
        this.createdAt = createdAt;
    }

    public Long getRequestId() { return requestId; }
    public void setRequestId(Long requestId) { this.requestId = requestId; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public User getReceiver() { return receiver; }
    public void setReceiver(User receiver) { this.receiver = receiver; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}