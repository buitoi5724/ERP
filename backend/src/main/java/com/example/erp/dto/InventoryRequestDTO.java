package com.example.erp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class InventoryRequestDTO {

    // ===== REQUIRED =====
    private Long productId;
    private Integer quantity;
    private String warehouse;
    private Long id; // optional, giữ nguyên

    // ===== PRICE =====
    private BigDecimal costPrice;
    private BigDecimal salePrice;

    // ===== STOCK RULE =====
    private Integer minStock;
    private Integer maxStock;

    // ===== META =====
    private String status;
    private String note;

    // ===== TIME (OPTIONAL) =====
    private LocalDateTime actionDate;

    // ===== INVENTORY ITEM INFO (MỚI) =====
    private Long supplierId;         // Nhà cung cấp
    private String batchNumber;      // Lô hàng
    private String serialNumber;     // Serial
    private LocalDate expirationDate; // Hạn dùng
    private boolean createItem;      // Flag: có tạo InventoryItem không

    // ===== GETTER / SETTER =====

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getWarehouse() { return warehouse; }
    public void setWarehouse(String warehouse) { this.warehouse = warehouse; }

    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }

    public BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }

    public Integer getMinStock() { return minStock; }
    public void setMinStock(Integer minStock) { this.minStock = minStock; }

    public Integer getMaxStock() { return maxStock; }
    public void setMaxStock(Integer maxStock) { this.maxStock = maxStock; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getActionDate() { return actionDate; }
    public void setActionDate(LocalDateTime actionDate) { this.actionDate = actionDate; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // ===== INVENTORY ITEM GETTER / SETTER =====
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }

    public boolean isCreateItem() { return createItem; }
    public void setCreateItem(boolean createItem) { this.createItem = createItem; }
    
}
