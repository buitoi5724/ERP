package com.example.erp.entity;

import com.example.erp.util.BaseEntity;
import com.example.erp.util.InventoryAction;
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_transaction")
public class InventoryTransaction extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    // ====== LIÊN KẾT NGHIỆP VỤ ======
    @Column(nullable = false)
    private Long inventoryId;

    private Long inventoryItemId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false, length = 50)
    private String warehouse;

    private Long customerId;

    // ====== SỐ LƯỢNG ======
    @Column(nullable = false)
    private Integer quantity; // + hoặc -

    private Integer beforeQuantity;
    private Integer afterQuantity;

    // ====== HÀNH ĐỘNG ======
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InventoryAction action;

    // ====== THỜI GIAN ======
    @Column(nullable = false)
    private LocalDateTime transactionDate;

    // ====== GHI CHÚ ======
    private String note;

    // ====== GETTERS / SETTERS =====
    public Long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }

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

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getBeforeQuantity() {
        return beforeQuantity;
    }

    public void setBeforeQuantity(Integer beforeQuantity) {
        this.beforeQuantity = beforeQuantity;
    }

    public Integer getAfterQuantity() {
        return afterQuantity;
    }

    public void setAfterQuantity(Integer afterQuantity) {
        this.afterQuantity = afterQuantity;
    }

    public InventoryAction getAction() {
        return action;
    }

    public void setAction(InventoryAction action) {
        this.action = action;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

	public void setUnitPrice(BigDecimal unitPrice) {
		// TODO Auto-generated method stub
		
	}

	public void setReferenceCode(String referenceCode) {
		// TODO Auto-generated method stub
		
	}
}
