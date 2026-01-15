package com.example.erp.entity;

import java.time.LocalDateTime;
import com.example.erp.util.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "inventory_log")
public class InventoryLog extends BaseEntity {

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantityChange; // + nhập | - xuất | + giữ | - hủy giữ

    @Column(nullable = false)
    private String type; // IN, OUT, ADJUST, RESERVE, RELEASE

    @Column(nullable = false)
    private String warehouse;

    @Column(name = "action_time")
    private LocalDateTime actionTime = LocalDateTime.now();

    public InventoryLog() {}

    public InventoryLog(Long productId, Integer quantityChange, String type, String warehouse) {
        this.productId = productId;
        this.quantityChange = quantityChange;
        this.type = type;
        this.warehouse = warehouse;
        this.actionTime = LocalDateTime.now();
    }

    // ===== GETTERS / SETTERS =====
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getQuantityChange() { return quantityChange; }
    public void setQuantityChange(Integer quantityChange) { this.quantityChange = quantityChange; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getWarehouse() { return warehouse; }
    public void setWarehouse(String warehouse) { this.warehouse = warehouse; }

    public LocalDateTime getActionTime() { return actionTime; }
    public void setActionTime(LocalDateTime actionTime) { this.actionTime = actionTime; }
}
