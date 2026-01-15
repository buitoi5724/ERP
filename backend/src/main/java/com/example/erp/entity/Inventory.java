package com.example.erp.entity;

import com.example.erp.util.BaseEntity;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "inventory",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "warehouse"})
    }
)
public class Inventory extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "product_id")
    private Long productId;
    
   
    // ===== Phiếu nhập kho =====
    @Column(name = "receipt_code")
    private String receiptCode;

    // ===== Snapshot thông tin sản phẩm =====
    @Column(name = "product_code")
    private String productCode;

    @Column(name = "product_name")
    private String productName;

    // ===== Số lượng =====
    @Column(nullable = false)
    private Integer quantity = 0;

    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity = 0;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity = 0;

    // ===== Giá =====
    @Column(name = "cost_price")
    private BigDecimal costPrice;

    @Column(name = "sale_price")
    private BigDecimal salePrice;

    // ===== Cảnh báo tồn =====
    @Column(name = "min_stock")
    private Integer minStock = 0;

    @Column(name = "max_stock")
    private Integer maxStock;

    // ===== Kho =====
    @Column(nullable = false)
    private String warehouse = "DEFAULT";

    // ===== Thời điểm nghiệp vụ =====
    @Column(name = "last_import_date")
    private LocalDateTime lastImportDate;

    @Column(name = "last_export_date")
    private LocalDateTime lastExportDate;

    // ===== Trạng thái & ghi chú =====
    @Column
    private String status;

    @Column(columnDefinition = "NVARCHAR(500)")
    private String note;

    // ===== Getter / Setter =====
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
   
    public String getReceiptCode() { return receiptCode; }
    public void setReceiptCode(String receiptCode) { this.receiptCode = receiptCode; }

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        recalcAvailable();
    }

    public Integer getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
        recalcAvailable();
    }

    public Integer getAvailableQuantity() { return availableQuantity; }

    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }

    public BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }

    public Integer getMinStock() { return minStock; }
    public void setMinStock(Integer minStock) { this.minStock = minStock; }

    public Integer getMaxStock() { return maxStock; }
    public void setMaxStock(Integer maxStock) { this.maxStock = maxStock; }

    public String getWarehouse() { return warehouse; }
    public void setWarehouse(String warehouse) { this.warehouse = warehouse; }

    public LocalDateTime getLastImportDate() { return lastImportDate; }
    public void setLastImportDate(LocalDateTime lastImportDate) { this.lastImportDate = lastImportDate; }

    public LocalDateTime getLastExportDate() { return lastExportDate; }
    public void setLastExportDate(LocalDateTime lastExportDate) { this.lastExportDate = lastExportDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    // ===== Giá trị tồn kho =====
    @Transient
    public BigDecimal getInventoryValue() {
        if (costPrice == null || quantity == null) return BigDecimal.ZERO;
        return costPrice.multiply(BigDecimal.valueOf(quantity));
    }

    // ===== Logic tính availableQuantity =====
    private void recalcAvailable() {
        this.availableQuantity = Math.max(
            (quantity != null ? quantity : 0) - (reservedQuantity != null ? reservedQuantity : 0), 0
        );
    }
	public void setCreatedDate(LocalDate now) {
		// TODO Auto-generated method stub
		
	}
	public void setUpdatedDate(LocalDate now) {
		// TODO Auto-generated method stub
		
	}
}
