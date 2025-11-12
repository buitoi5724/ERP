// File: src/main/java/com/example/erp/dto/OrderItemDTO.java
package com.example.erp.dto;

public class OrderItemDTO {
    
    private Long productId;
    private String productName;
    private int quantity;
    private double price;
    // (Bạn có thể thêm imageUrl nếu cần)

    // === TẠO TẤT CẢ GETTERS VÀ SETTERS ===
    // (Dùng IDE tự động tạo)

    public Long getProductId() { return productId; }
    public void setProductId(Long long1) { this.productId = long1; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}