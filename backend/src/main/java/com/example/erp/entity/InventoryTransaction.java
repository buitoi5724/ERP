package com.example.erp.entity;

import com.example.erp.util.BaseEntity;
import com.example.erp.util.InventoryAction;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_transaction")
public class InventoryTransaction extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String productName;
    private int quantity;
    private LocalDateTime transactionDate;

    @Enumerated(EnumType.STRING)
    private InventoryAction action;

    public InventoryTransaction() {}

    public InventoryTransaction(String productName, int quantity, LocalDateTime transactionDate, InventoryAction action) {
        this.productName = productName;
        this.quantity = quantity;
        this.transactionDate = transactionDate;
        this.action = action;
    }

    // ===== GETTERS / SETTERS =====
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    public InventoryAction getAction() { return action; }
    public void setAction(InventoryAction action) { this.action = action; }

    // id đã có sẵn từ BaseEntity
}
