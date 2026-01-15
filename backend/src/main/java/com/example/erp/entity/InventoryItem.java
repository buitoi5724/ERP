package com.example.erp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.example.erp.util.BaseEntity;

@Entity
@Table(name = "inventory_item")
public class InventoryItem extends BaseEntity {

    /* =====================================================
     *  KHO / PHIẾU NHẬP
     * ===================================================== */
    @Column(name = "inventory_id", nullable = false)
    private Long inventoryId;

    @Column(name = "received_date", nullable = false)
    private LocalDate receivedDate;
    @Column(name = "manufacture_date")
    private LocalDate manufactureDate;
    
    /* =====================================================
     *  SẢN PHẨM (DÙNG ID – SERVICE ĐANG DÙNG)
     * ===================================================== */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    // snapshot – optional
    @Column(name = "product_name")
    private String productName;

    /* =====================================================
     *  GIÁ (CHUẨN ERP – DÙNG THỰC TẾ)
     * ===================================================== */
    @Column(name = "import_price", precision = 19, scale = 2)
    private BigDecimal importPrice;   // giá nhập 1 đơn vị

    @Column(name = "unit_price", precision = 19, scale = 2)
    private BigDecimal unitPrice;     // dùng cho addItem()

    @Column(name = "total_price", precision = 19, scale = 2)
    private BigDecimal totalPrice;    // unit/import * quantity

    /* =====================================================
     *  ⚠️ FIELD CŨ – GIỮ ĐỂ KHÔNG VỠ DB
     * ===================================================== */
    @Deprecated
    @Column(name = "cost_price")
    private Double costPrice;

    @Deprecated
    @Column(name = "line_total")
    private Double lineTotal;

    /* =====================================================
     *  SỐ LƯỢNG
     * ===================================================== */
    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "remaining_quantity", nullable = false)
    private Integer remainingQuantity;

    /* =====================================================
     *  LÔ – SERIAL – HSD
     * ===================================================== */
    @Column(name = "batch_number")
    private String batchNumber;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    /* =====================================================
     *  NGUỒN / ĐÍCH
     * ===================================================== */
    @Column(name = "supplier_id")
    private Long supplierId;

    @Column(name = "customer_id")
    private Long customerId;

    /* =====================================================
     *  TRẠNG THÁI
     * ===================================================== */
    @Column(nullable = false)
    private String status;
    // AVAILABLE | SOLD | DAMAGED | EXPIRED

    /* =====================================================
     *  GETTER / SETTER – ĐỦ ĐỂ SERVICE CHẠY
     * ===================================================== */

    
    public Long getInventoryId() { return inventoryId; }
    /**
	 * @return the manufactureDate
	 */
	public LocalDate getManufactureDate() {
		return manufactureDate;
	}
	/**
	 * @param manufactureDate the manufactureDate to set
	 */
	public void setManufactureDate(LocalDate manufactureDate) {
		this.manufactureDate = manufactureDate;
	}
	public void setInventoryId(Long inventoryId) { this.inventoryId = inventoryId; }

    public LocalDate getReceivedDate() { return receivedDate; }
    public void setReceivedDate(LocalDate receivedDate) { this.receivedDate = receivedDate; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

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

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
