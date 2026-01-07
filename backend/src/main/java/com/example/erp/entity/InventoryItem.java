package com.example.erp.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

import com.example.erp.util.BaseEntity;

@Entity
@Table(name = "inventory_item")
public class InventoryItem extends BaseEntity {

    // =========================
    // LIÊN KẾT INVENTORY
    // =========================
    @Column(name = "inventory_id", nullable = false)
    private Long inventoryId;

    // =========================
    // THÔNG TIN LÔ / SERIAL
    // =========================
    @Column(name = "batch_number")
    private String batchNumber;

    @Column(name = "serial_number", unique = true)
    private String serialNumber;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "received_date", nullable = false)
    private LocalDate receivedDate;

    // =========================
    // SỐ LƯỢNG (🔥 BẮT BUỘC)
    // =========================
    @Column(nullable = false)
    private Integer quantity;               // số lượng nhập ban đầu

    @Column(name = "remaining_quantity", nullable = false)
    private Integer remainingQuantity;      // số lượng còn lại (FIFO)

    // =========================
    // ĐỐI TƯỢNG LIÊN QUAN
    // =========================
    @Column(name = "supplier_id")
    private Long supplierId;

    @Column(name = "customer_id")
    private Long customerId;

    // =========================
    // TRẠNG THÁI LÔ
    // =========================
    @Column(nullable = false)
    private String status; 
    // AVAILABLE | RESERVED | SOLD | DAMAGED | EXPIRED

    // =========================
    // GETTERS & SETTERS
    // =========================
    public Long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
