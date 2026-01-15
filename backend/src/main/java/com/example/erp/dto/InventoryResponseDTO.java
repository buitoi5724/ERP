package com.example.erp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InventoryResponseDTO {

    // ===== PRODUCT SNAPSHOT =====
    private Long productId;
    private String productCode;
    private String productName;

    // ===== QUANTITY =====
    private Integer quantity;
    private Integer reservedQuantity;
    private Integer availableQuantity;

    // ===== PRICE =====
    private BigDecimal costPrice;
    private BigDecimal salePrice;

    // ===== STOCK RULE =====
    private Integer minStock;
    private Integer maxStock;

    // ===== META =====
    private String warehouse;
    private String status;
    private String note;

    // ===== TIME =====
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private LocalDateTime lastImportDate;
    private LocalDateTime lastExportDate;

    
    private BigDecimal inventoryValue;

    public BigDecimal getInventoryValue() { return inventoryValue; }
    public void setInventoryValue(BigDecimal inventoryValue) { this.inventoryValue = inventoryValue; }

    // ===== GETTER / SETTER =====

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(Integer reservedQuantity) { this.reservedQuantity = reservedQuantity; }

    public Integer getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(Integer availableQuantity) { this.availableQuantity = availableQuantity; }

    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }

    public BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }

    public Integer getMinStock() { return minStock; }
    public void setMinStock(Integer minStock) { this.minStock = minStock; }

    public Integer getMaxStock() { return maxStock; }
    public void setMaxStock(Integer maxStock) { this.maxStock = maxStock; }

    public String getWarehouse() { return warehouse; }
    public void setWarehouse(String warehouse) { this.warehouse = warehouse; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }

    public LocalDateTime getLastImportDate() { return lastImportDate; }
    public void setLastImportDate(LocalDateTime lastImportDate) { this.lastImportDate = lastImportDate; }

    public LocalDateTime getLastExportDate() { return lastExportDate; }
    public void setLastExportDate(LocalDateTime lastExportDate) { this.lastExportDate = lastExportDate; }
}
