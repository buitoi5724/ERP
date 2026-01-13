package com.example.erp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class InventoryItemResponseDTO {

    // ===== ID =====
    private Long itemId;
    private Long inventoryId;
    private Long productId;
    private String productName;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    
    
    // ===== KHO =====
    private String warehouse;
    private String warehouseName;   // mới
    
    // ===== NHẬP / XUẤT =====
    private Integer quantity;              // số lượng nhập
    private Integer remainingQuantity;     // còn lại (FIFO)
    private Long supplierId;
    private Long customerId;
    private String supplierName;    // mới
    
    // ===== THÔNG TIN LÔ =====
    private String serialNumber;
    private String batchNumber;
    private LocalDate expirationDate;
    private LocalDate receivedDate;

    // ===== TRẠNG THÁI =====
    private String status; // AVAILABLE, SOLD, DAMAGED, EXPIRED

    // ===== AUDIT =====
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    // ===== GETTERS & SETTERS =====
    public Long getItemId() {
        return itemId;
    }

    
    /**
	 * @return the warehouseName
	 */
	public String getWarehouseName() {
		return warehouseName;
	}


	/**
	 * @param warehouseName the warehouseName to set
	 */
	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}


	public BigDecimal getUnitPrice() {
	    return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
	    this.unitPrice = unitPrice;
	}

	public BigDecimal getTotalPrice() {
	    return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
	    this.totalPrice = totalPrice;
	}



	/**
	 * @return the supplierName
	 */
	public String getSupplierName() {
		return supplierName;
	}


	/**
	 * @param supplierName the supplierName to set
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}


	public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(Integer remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
}
