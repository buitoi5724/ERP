package com.example.erp.dto;

import java.math.BigDecimal;

public class ImportedInventoryItemDTO {

    private Long productId;
    private Long inventoryItemId;

    private Integer quantity;          // số lượng nhập
    private BigDecimal importPrice;    // đơn giá nhập
    private Long supplierId; // 👈 THÊM
    
    // =====================
    // GETTER / SETTER
    // =====================

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getInventoryItemId() {
        return inventoryItemId;
    }

    public void setInventoryItemId(Long inventoryItemId) {
        this.inventoryItemId = inventoryItemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getImportPrice() {
        return importPrice;
    }

    public void setImportPrice(BigDecimal importPrice) {
        this.importPrice = importPrice;
    }
}
