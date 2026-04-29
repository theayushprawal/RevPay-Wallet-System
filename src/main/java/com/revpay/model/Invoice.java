package com.revpay.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.revpay.model.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "INVOICES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inv_seq")
    @SequenceGenerator(name = "inv_seq", sequenceName = "GEN_INV_ID", allocationSize = 1)
    @Column(name = "INVOICE_ID")
    private Long invoiceId;

    // OPTIMIZATION: @ManyToOne is EAGER by default. Changed to LAZY.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUSINESS_ID")
    private User business;

    @Column(name = "CUSTOMER_ID")
    private Long customerId;

    @Column(name = "CUSTOMER_NAME")
    private String customerName;

    @Column(name = "TOTAL_AMOUNT")
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private InvoiceStatus status;

    @Column(name = "DUE_DATE")
    private LocalDate dueDate;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    // @OneToMany is LAZY by default, but writing it explicitly is best practice.
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InvoiceItem> items;
}