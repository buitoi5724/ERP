package com.example.erp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.erp.util.BaseEntity;
import com.example.erp.util.InventoryItemStatus;

@Entity
@Table(name = "inventory_item")
public class InventoryItem extends BaseEntity {

    @Column(name = "inventory_id", nullable = false)
    private Long inventoryId;

    @Column(name = "received_date", nullable = false)
    private LocalDate receivedDate;

    @Column(name = "manufacture_date")
    private LocalDate manufactureDate;

    @Column(name = "product_id", nullable = false)
    private Long productId;

  
    @Column(name = "import_price", precision = 19, scale = 2)
    private BigDecimal importPrice;

    @Column(name = "unit_price", precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_price", precision = 19, scale = 2)
    private BigDecimal totalPrice;

   

  

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "remaining_quantity", nullable = false)
    private Integer remainingQuantity;

    @Column(name = "batch_number")
    private String batchNumber;

  

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "supplier_id")
    private Long supplierId;

    @Column(name = "customer_id")
    private Long customerId;

    /* =====================================================
     *  ENUM STATUS
     * ===================================================== */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InventoryItemStatus status = InventoryItemStatus.AVAILABLE;

    /* ================= GETTER / SETTER ================= */

    public Long getInventoryId() { return inventoryId; }
    public void setInventoryId(Long inventoryId) { this.inventoryId = inventoryId; }

    public LocalDate getReceivedDate() { return receivedDate; }
    public void setReceivedDate(LocalDate receivedDate) { this.receivedDate = receivedDate; }

    public LocalDate getManufactureDate() { return manufactureDate; }
    public void setManufactureDate(LocalDate manufactureDate) { this.manufactureDate = manufactureDate; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }


    public BigDecimal getImportPrice() { return importPrice; }
    public void setImportPrice(BigDecimal importPrice) { this.importPrice = importPrice; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getRemainingQuantity() { return remainingQuantity; }
    public void setRemainingQuantity(Integer remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }


    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public InventoryItemStatus getStatus() { return status; }
    public void setStatus(InventoryItemStatus status) { this.status = status; }
}
