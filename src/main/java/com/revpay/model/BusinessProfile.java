package com.revpay.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.revpay.model.enums.YesNoStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "BUSINESS_PROFILES")
public class BusinessProfile {

    @Id
    @Column(name = "BUSINESS_ID")
    private Long businessId;

    @JsonIgnore
    @OneToOne
    @MapsId
    @JoinColumn(name = "BUSINESS_ID")
    private User user;

    @Column(name = "BUSINESS_NAME")
    private String businessName;

    @Column(name = "BUSINESS_TYPE")
    private String businessType;

    @Column(name = "PAN_NUMBER")
    private String panNumber;

    @Column(name = "ADDRESS")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "VERIFIED")
    private YesNoStatus verified;

    // NEW FIELD (Verification document simulation)
    @Column(name = "VERIFICATION_DOCUMENT")
    private String verificationDocument;

    @Enumerated(EnumType.STRING)
    @Column(name = "DOCUMENT_UPLOADED")
    private YesNoStatus documentUploaded;

    public BusinessProfile() {}

    public BusinessProfile(Long businessId, User user, String businessName,
                           String businessType, String panNumber,
                           String address, YesNoStatus verified,
                           String verificationDocument, YesNoStatus documentUploaded) {

        this.businessId = businessId;
        this.user = user;
        this.businessName = businessName;
        this.businessType = businessType;
        this.panNumber = panNumber;
        this.address = address;
        this.verified = verified;
        this.verificationDocument = verificationDocument;
        this.documentUploaded = documentUploaded;
    }

    public Long getBusinessId() { return businessId; }
    public void setBusinessId(Long businessId) { this.businessId = businessId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }

    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public YesNoStatus getVerified() { return verified; }
    public void setVerified(YesNoStatus verified) { this.verified = verified; }

    public String getVerificationDocument() { return verificationDocument; }
    public void setVerificationDocument(String verificationDocument) { this.verificationDocument = verificationDocument; }

    public YesNoStatus getDocumentUploaded() { return documentUploaded; }
    public void setDocumentUploaded(YesNoStatus documentUploaded) { this.documentUploaded = documentUploaded; }
}