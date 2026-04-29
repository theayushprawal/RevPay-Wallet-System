package com.revpay.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.revpay.model.enums.PaymentMethodType;
import com.revpay.model.enums.YesNoStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PAYMENT_METHODS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pm_seq")
    @SequenceGenerator(name = "pm_seq", sequenceName = "GEN_PM_ID", allocationSize = 1)
    @Column(name = "PM_ID")
    private Long pmId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY) // OPTIMIZATION: Changed from EAGER (default) to LAZY
    @JoinColumn(name = "USER_ID")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "METHOD_TYPE")
    private PaymentMethodType methodType;

    @Column(name = "DETAILS_ENC")
    private String detailsEnc;

    @Column(name = "PAYMENT_NAME")
    private String paymentName;

    @Column(name = "CVV")
    private String cvv;

    @Column(name = "BILLING_ADDRESS")
    private String billingAddress;

    @Column(name = "LAST4")
    private String last4;

    @Column(name = "EXPIRY_DATE")
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "IS_DEFAULT")
    private YesNoStatus isDefault;

    @Column(name = "IFSC_CODE")
    private String ifscCode;
}