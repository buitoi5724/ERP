package com.example.erp.entity;

import com.example.erp.util.BaseEntity;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(
    name = "inventory",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "warehouse"})
    }
)
public class Inventory extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    // ====== Liên kết sản phẩm ======
    @Column(name = "product_id", nullable = false)
    private Long productId;

    // ====== Snapshot thông tin sản phẩm (THÊM) ======
    @Column(name = "product_code")
    private String productCode;

    @Column(name = "product_name")
    private String productName;

    // ====== Số lượng ======
    @Column(nullable = false)
    private Integer quantity = 0;

    @Transient
    public BigDecimal getInventoryValue() {
        if (costPrice == null || quantity == null) return BigDecimal.ZERO;
        return costPrice.multiply(BigDecimal.valueOf(quantity));
    }
    
    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity = 0;

    // GIỮ NGUYÊN – KHÔNG ĐỔI LOGIC
    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity = 0;

    // ====== Giá (THÊM) ======
    @Column(name = "cost_price")
    private BigDecimal costPrice;

    @Column(name = "sale_price")
    private BigDecimal salePrice;

    // ====== Cảnh báo tồn ======
    @Column(name = "min_stock")
    private Integer minStock = 0;

    @Column(name = "max_stock")
    private Integer maxStock;

    // ====== Kho ======
    @Column(nullable = false)
    private String warehouse = "DEFAULT";

    // ====== Thời điểm nghiệp vụ ======
    @Column(name = "last_import_date")
    private LocalDateTime lastImportDate;

    @Column(name = "last_export_date")
    private LocalDateTime lastExportDate;

    // ====== Trạng thái & ghi chú (THÊM) ======
    @Column
    private String status;

    @Column(columnDefinition = "NVARCHAR(500)")
    private String note;

    // ====== Getter / Setter ======
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        recalcAvailable(); // GIỮ NGUYÊN
    }

    public Integer getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
        recalcAvailable(); // GIỮ NGUYÊN
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

    // ====== BUSINESS LOGIC (GIỮ NGUYÊN 100%) ======
    private void recalcAvailable() {
        this.availableQuantity = Math.max(
            (quantity != null ? quantity : 0)
          - (reservedQuantity != null ? reservedQuantity : 0),
            0
        );
    }
}
