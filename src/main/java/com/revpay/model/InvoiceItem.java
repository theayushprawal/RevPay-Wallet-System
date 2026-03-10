package com.revpay.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "INVOICE_ITEMS")
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_seq")
    @SequenceGenerator(name = "item_seq", sequenceName = "GEN_ITEM_ID", allocationSize = 1)
    @Column(name = "ITEM_ID")
    private Long itemId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "INVOICE_ID")
    private Invoice invoice;

    @Column(name = "ITEM_NAME")
    private String itemName;

    @Column(name = "QUANTITY")
    private Integer quantity;

    @Column(name = "PRICE")
    private BigDecimal price;

    public InvoiceItem() {}

    public InvoiceItem(Long itemId, Invoice invoice,
                       String itemName, Integer quantity,
                       BigDecimal price) {
        this.itemId = itemId;
        this.invoice = invoice;
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
    }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public Invoice getInvoice() { return invoice; }
    public void setInvoice(Invoice invoice) { this.invoice = invoice; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}