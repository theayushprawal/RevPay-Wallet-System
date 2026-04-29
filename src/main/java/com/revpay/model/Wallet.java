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
@Table(name = "WALLETS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wallet_seq")
    @SequenceGenerator(name = "wallet_seq", sequenceName = "GEN_WALLET_ID", allocationSize = 1)
    @Column(name = "WALLET_ID")
    private Long walletId;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY) // OPTIMIZATION: Changed from EAGER (default) to LAZY
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(name = "BALANCE")
    private BigDecimal balance;

    @Column(name = "LAST_UPDATED")
    private LocalDateTime lastUpdated;
}