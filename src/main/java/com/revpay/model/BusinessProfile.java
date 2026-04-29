package com.revpay.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.revpay.model.enums.YesNoStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "BUSINESS_PROFILES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessProfile {

    @Id
    @Column(name = "BUSINESS_ID")
    private Long businessId;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY) // OPTIMIZATION: Changed from EAGER (default) to LAZY
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
}