package com.example.erp.dto;

public class InventoryDTO {
    private String productName;
    private int quantity;
    private double price;

    public InventoryDTO(String productName, int quantity, double price) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    // getters & setters
}
