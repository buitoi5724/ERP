package com.example.erp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InventoryRequestDTO {

    // ===== REQUIRED =====
    private Long productId;
    private Integer quantity;
    private String warehouse;

    // ===== PRICE =====
    private BigDecimal costPrice;
    private BigDecimal salePrice;
    private Long id;  
    // ===== STOCK RULE =====
    private Integer minStock;
    private Integer maxStock;

    // ===== META =====
    private String status;
    private String note;

    // ===== TIME (OPTIONAL) =====
    private LocalDateTime actionDate;

    // ===== GETTER / SETTER =====

    
    public Long getProductId() { return productId; }
    /**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
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
}
