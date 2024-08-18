package com.jaime.products.service;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCreatedEvent {

    private String productId;

    private String title;

    private BigDecimal price;

    private Integer quantity;

    public ProductCreatedEvent (String productId, String title, BigDecimal price, Integer quantity) {
        this.productId = productId;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
    }

}
