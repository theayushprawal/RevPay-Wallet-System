package com.revpay.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "INVOICE_ITEMS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_seq")
    @SequenceGenerator(name = "item_seq", sequenceName = "GEN_ITEM_ID", allocationSize = 1)
    @Column(name = "ITEM_ID")
    private Long itemId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY) // OPTIMIZATION: Changed from EAGER (default) to LAZY
    @JoinColumn(name = "INVOICE_ID")
    private Invoice invoice;

    @Column(name = "ITEM_NAME")
    private String itemName;

    @Column(name = "QUANTITY")
    private Integer quantity;

    @Column(name = "PRICE")
    private BigDecimal price;
}