package com.example.erp.dto;

import java.time.LocalDate;
import java.util.List;

public class InventoryExportRequestDTO {

    private Long inventoryId;      // 👈 QUAN TRỌNG
    private LocalDate date;
    private String note;
    private Long customerId;
    private String warehouse;
    private List<ExportItemDTO> items;

    public Long getInventoryId() {
        return inventoryId;
    }
    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }

    public Long getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
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
	public List<ExportItemDTO> getItems() {
        return items;
    }
    public void setItems(List<ExportItemDTO> items) {
        this.items = items;
    }
	public int getQuantity() {
		// TODO Auto-generated method stub
		return 0;
	}
}
