package com.revpay.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.revpay.model.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "MONEY_REQUESTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoneyRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mr_seq")
    @SequenceGenerator(name = "mr_seq", sequenceName = "GEN_REQ_ID", allocationSize = 1)
    @Column(name = "REQUEST_ID")
    private Long requestId;

    // OPTIMIZATION: Changed from EAGER (default) to LAZY
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SENDER_ID")
    private User sender;

    // OPTIMIZATION: Changed from EAGER (default) to LAZY
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECEIVER_ID")
    private User receiver;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private RequestStatus status;

    @Column(name = "EXPIRY_DATE")
    private LocalDateTime expiryDate;

    @Column(name = "REMARKS")
    private String remarks;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
}