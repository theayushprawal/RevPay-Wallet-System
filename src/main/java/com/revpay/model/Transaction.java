package com.revpay.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.revpay.model.enums.TransactionStatus;
import com.revpay.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TRANSACTIONS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "txn_seq")
    @SequenceGenerator(name = "txn_seq", sequenceName = "GEN_TXN_ID", allocationSize = 1)
    @Column(name = "TXN_ID")
    private Long txnId;

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
    @Column(name = "TXN_TYPE")
    private TransactionType txnType;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private TransactionStatus status;

    @Column(name = "REMARKS")
    private String remarks;

    @Column(name = "TXN_DATE")
    private LocalDateTime txnDate;
}