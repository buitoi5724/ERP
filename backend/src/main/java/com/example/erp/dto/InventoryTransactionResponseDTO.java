package com.example.erp.dto;

import java.time.LocalDateTime;

import com.example.erp.util.InventoryAction;

public class InventoryTransactionResponseDTO {

    private Long id;

    private Long productId;
    private Long inventoryId;
    private Long inventoryItemId;

    private String warehouse;
    private Integer quantity;

    private Integer beforeQuantity;
    private Integer afterQuantity;

    private InventoryAction action;

    private Long customerId;

    private LocalDateTime transactionDate;
    private String note;
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
	 * @return the inventoryId
	 */
	public Long getInventoryId() {
		return inventoryId;
	}
	/**
	 * @param inventoryId the inventoryId to set
	 */
	public void setInventoryId(Long inventoryId) {
		this.inventoryId = inventoryId;
	}
	/**
	 * @return the inventoryItemId
	 */
	public Long getInventoryItemId() {
		return inventoryItemId;
	}
	/**
	 * @param inventoryItemId the inventoryItemId to set
	 */
	public void setInventoryItemId(Long inventoryItemId) {
		this.inventoryItemId = inventoryItemId;
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
	 * @return the quantity
	 */
	public Integer getQuantity() {
		return quantity;
	}
	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	/**
	 * @return the beforeQuantity
	 */
	public Integer getBeforeQuantity() {
		return beforeQuantity;
	}
	/**
	 * @param beforeQuantity the beforeQuantity to set
	 */
	public void setBeforeQuantity(Integer beforeQuantity) {
		this.beforeQuantity = beforeQuantity;
	}
	/**
	 * @return the afterQuantity
	 */
	public Integer getAfterQuantity() {
		return afterQuantity;
	}
	/**
	 * @param afterQuantity the afterQuantity to set
	 */
	public void setAfterQuantity(Integer afterQuantity) {
		this.afterQuantity = afterQuantity;
	}
	/**
	 * @return the action
	 */
	public InventoryAction getAction() {
		return action;
	}
	/**
	 * @param action the action to set
	 */
	public void setAction(InventoryAction action) {
		this.action = action;
	}
	/**
	 * @return the customerId
	 */
	public Long getCustomerId() {
		return customerId;
	}
	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
	/**
	 * @return the transactionDate
	 */
	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}
	/**
	 * @param transactionDate the transactionDate to set
	 */
	public void setTransactionDate(LocalDateTime transactionDate) {
		this.transactionDate = transactionDate;
	}
	/**
	 * @return the note
	 */
	public String getNote() {
		return note;
	}
	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
	}

    // getters & setters
    
    
}
