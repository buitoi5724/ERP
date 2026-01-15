package com.example.erp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class InventoryImportRequestDTO {

    private Long inventoryId;
    private Long supplierId;
    private BigDecimal importPrice; 
    private BigDecimal totalAmount; // tổng tiền phiếu nhập
    
    private String receiptCode;
    private String warehouse;
    private LocalDate receivedDate;
    private String note;
    private String date;
    
    
    
    private List<InventoryItemRequestDTO> items;
	/**
	 * @return the inventoryId
	 */
	public Long getInventoryId() {
		return inventoryId;
	}
	
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
	/**
	 * @return the receiptCode
	 */
	public String getReceiptCode() {
		return receiptCode;
	}


	/**
	 * @param receiptCode the receiptCode to set
	 */
	public void setReceiptCode(String receiptCode) {
		this.receiptCode = receiptCode;
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
	 * @return the receivedDate
	 */
	public LocalDate getReceivedDate() {
		return receivedDate;
	}


	/**
	 * @param receivedDate the receivedDate to set
	 */
	public void setReceivedDate(LocalDate receivedDate) {
		this.receivedDate = receivedDate;
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


	/**
	 * @param inventoryId the inventoryId to set
	 */
	public void setInventoryId(Long inventoryId) {
		this.inventoryId = inventoryId;
	}
	/**
	 * @return the supplierId
	 */
	public Long getSupplierId() {
		return supplierId;
	}
	
	/**
	 * @return the importPrice
	 */
	public BigDecimal getImportPrice() {
		return importPrice;
	}
	/**
	 * @param importPrice the importPrice to set
	 */
	public void setImportPrice(BigDecimal importPrice) {
		this.importPrice = importPrice;
	}
	/**
	 * 
	 * @param supplierId the supplierId to set
	 */
	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}
	/**
	 * @return the totalAmount
	 */
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	/**
	 * @param totalAmount the totalAmount to set
	 */
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	/**
	 * @return the items
	 */
	public List<InventoryItemRequestDTO> getItems() {
		return items;
	}
	/**
	 * @param items the items to set
	 */
	public void setItems(List<InventoryItemRequestDTO> items) {
		this.items = items;
	}

    // getters & setters
    
    
    
}
