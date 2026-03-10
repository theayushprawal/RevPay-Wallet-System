package com.revpay.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.revpay.model.enums.PaymentMethodType;
import com.revpay.model.enums.YesNoStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "PAYMENT_METHODS")
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pm_seq")
    @SequenceGenerator(name = "pm_seq", sequenceName = "GEN_PM_ID", allocationSize = 1)
    @Column(name = "PM_ID")
    private Long pmId;

    @JsonIgnore
    @ManyToOne
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

    public PaymentMethod() {}

    public PaymentMethod(Long pmId, User user, PaymentMethodType methodType,
                         String detailsEnc, String paymentName,
                         String cvv, String billingAddress,
                         String last4, LocalDate expiryDate,
                         YesNoStatus isDefault, String ifscCode) {
        this.pmId = pmId;
        this.user = user;
        this.methodType = methodType;
        this.detailsEnc = detailsEnc;
        this.paymentName = paymentName;
        this.cvv = cvv;
        this.billingAddress = billingAddress;
        this.last4 = last4;
        this.expiryDate = expiryDate;
        this.isDefault = isDefault;
        this.ifscCode = ifscCode;
    }

    public Long getPmId() { return pmId; }
    public void setPmId(Long pmId) { this.pmId = pmId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public PaymentMethodType getMethodType() { return methodType; }
    public void setMethodType(PaymentMethodType methodType) { this.methodType = methodType; }

    public String getDetailsEnc() { return detailsEnc; }
    public void setDetailsEnc(String detailsEnc) { this.detailsEnc = detailsEnc; }

    public String getPaymentName() { return paymentName; }
    public void setPaymentName(String paymentName) { this.paymentName = paymentName; }

    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }

    public String getBillingAddress() { return billingAddress; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }

    public String getLast4() { return last4; }
    public void setLast4(String last4) { this.last4 = last4; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public YesNoStatus getIsDefault() { return isDefault; }
    public void setIsDefault(YesNoStatus isDefault) { this.isDefault = isDefault; }

    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }
}