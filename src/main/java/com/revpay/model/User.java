package com.revpay.model;

import java.time.LocalDateTime;
import java.util.List;

import com.revpay.model.enums.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "USERS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    // OPTIMIZATION: Changed from EAGER (default) to LAZY
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BusinessProfile businessProfile;

    // OPTIMIZATION: Changed from EAGER (default) to LAZY
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Wallet wallet;

    // @OneToMany is LAZY by default, adding explicitly for consistency
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaymentMethod> paymentMethods;

    // OPTIMIZATION: Changed from EAGER (default) to LAZY
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private SecurityQuestion securityQuestion;
}