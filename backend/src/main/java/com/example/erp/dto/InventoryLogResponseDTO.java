package com.example.erp.dto;

import java.time.LocalDateTime;

public class InventoryLogResponseDTO {

    private Long id;
    private Long productId;
    private Integer quantityChange;
    private String type;
    private String warehouse;
    private LocalDateTime actionTime;
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
	/**
	 * @return the productId
	 */
	public Long getProductId() {
		return productId;
	}
	/**
	 * @param productId the productId to set
	 */
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	/**
	 * @return the quantityChange
	 */
	public Integer getQuantityChange() {
		return quantityChange;
	}
	/**
	 * @param quantityChange the quantityChange to set
	 */
	public void setQuantityChange(Integer quantityChange) {
		this.quantityChange = quantityChange;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the warehouse
	 */
	public String getWarehouse() {
		return warehouse;
	}
	/**
	 * @param warehouse the warehouse to set
	 */
	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}
	/**
	 * @return the actionTime
	 */
	public LocalDateTime getActionTime() {
		return actionTime;
	}
	/**
	 * @param actionTime the actionTime to set
	 */
	public void setActionTime(LocalDateTime actionTime) {
		this.actionTime = actionTime;
	}

    // getters & setters
    
}
