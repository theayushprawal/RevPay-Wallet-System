package com.revpay.dto;

import java.math.BigDecimal;

public class InvoiceItemRequest {

    private String itemName;
    private Integer quantity;
    private BigDecimal price;

    public InvoiceItemRequest() {}

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}