package com.revpay.model;

import java.time.LocalDateTime;
import java.util.List;

import com.revpay.model.enums.*;
import jakarta.persistence.*;

@Entity
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "GEN_USER_ID", allocationSize = 1)
    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "FULL_NAME", nullable = false)
    private String fullName;

    @Column(name = "EMAIL", unique = true, nullable = false)
    private String email;

    @Column(name = "PHONE", unique = true, nullable = false)
    private String phone;

    @Column(name = "PASSWORD_HASH", nullable = false)
    private String passwordHash;

    @Column(name = "TRANSACTION_PIN_HASH")
    private String transactionPinHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_TYPE")
    private UserType userType;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private UserStatus status;

    @Column(name = "FAILED_ATTEMPTS")
    private Integer failedAttempts;

    @Enumerated(EnumType.STRING)
    @Column(name = "IS_LOCKED")
    private YesNoStatus isLocked;

    @Column(name = "LAST_LOGIN")
    private LocalDateTime lastLogin;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private BusinessProfile businessProfile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Wallet wallet;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PaymentMethod> paymentMethods;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<SecurityQuestion> securityQuestions;

    // ===== DEFAULT CONSTRUCTOR (Required by JPA) =====
    public User() {}

    // ===== PARAMETERIZED CONSTRUCTOR (All fields) =====
    public User(Long userId, String fullName, String email, String phone,
                String passwordHash, String transactionPinHash,
                UserType userType, UserStatus status,
                Integer failedAttempts, YesNoStatus isLocked,
                LocalDateTime lastLogin, LocalDateTime createdAt) {

        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.transactionPinHash = transactionPinHash;
        this.userType = userType;
        this.status = status;
        this.failedAttempts = failedAttempts;
        this.isLocked = isLocked;
        this.lastLogin = lastLogin;
        this.createdAt = createdAt;
    }

    // ===== GETTERS & SETTERS =====

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getTransactionPinHash() { return transactionPinHash; }
    public void setTransactionPinHash(String transactionPinHash) { this.transactionPinHash = transactionPinHash; }

    public UserType getUserType() { return userType; }
    public void setUserType(UserType  userType) { this.userType = userType; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public Integer getFailedAttempts() { return failedAttempts; }
    public void setFailedAttempts(Integer failedAttempts) { this.failedAttempts = failedAttempts; }

    public YesNoStatus getIsLocked() { return isLocked; }
    public void setIsLocked(YesNoStatus isLocked) { this.isLocked = isLocked; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public BusinessProfile getBusinessProfile() { return businessProfile; }
    public void setBusinessProfile(BusinessProfile businessProfile) { this.businessProfile = businessProfile; }

    public Wallet getWallet() { return wallet; }
    public void setWallet(Wallet wallet) { this.wallet = wallet; }

    public List<PaymentMethod> getPaymentMethods() { return paymentMethods; }
    public void setPaymentMethods(List<PaymentMethod> paymentMethods) { this.paymentMethods = paymentMethods; }

    public List<SecurityQuestion> getSecurityQuestions() { return securityQuestions; }
    public void setSecurityQuestions(List<SecurityQuestion> securityQuestions) { this.securityQuestions = securityQuestions; }
}