package com.example.erp.dto;

import java.math.BigDecimal;

public class ExportedInventoryItemDTO {

    private Long inventoryItemId; // 👈 QUAN TRỌNG: truy ngược inventory
    private Long productId;
    private Integer quantity;
    private BigDecimal sellPrice; // 👈 giá bán tại thời điểm xuất

    // ===== GET / SET =====

    public Long getInventoryItemId() {
        return inventoryItemId;
    }

    public void setInventoryItemId(Long inventoryItemId) {
        this.inventoryItemId = inventoryItemId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(BigDecimal sellPrice) {
        this.sellPrice = sellPrice;
    }
}
