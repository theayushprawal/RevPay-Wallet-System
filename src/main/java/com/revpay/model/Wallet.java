package com.revpay.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "WALLETS")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wallet_seq")
    @SequenceGenerator(name = "wallet_seq", sequenceName = "GEN_WALLET_ID", allocationSize = 1)
    @Column(name = "WALLET_ID")
    private Long walletId;

    @OneToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Column(name = "BALANCE")
    private BigDecimal balance;

    @Column(name = "LAST_UPDATED")
    private LocalDateTime lastUpdated;

    public Wallet() {}

    public Wallet(Long walletId, User user, BigDecimal balance, LocalDateTime lastUpdated) {
        this.walletId = walletId;
        this.user = user;
        this.balance = balance;
        this.lastUpdated = lastUpdated;
    }

    public Long getWalletId() { return walletId; }
    public void setWalletId(Long walletId) { this.walletId = walletId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}