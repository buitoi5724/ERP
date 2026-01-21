package com.example.erp.entity;

import com.example.erp.util.BaseEntity;
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.erp.util.InventoryStatus;
@Entity
@Table(
    name = "inventory",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "warehouse"})
    },
    indexes = {
        @Index(name = "idx_inventory_product_warehouse", columnList = "product_id, warehouse")
    }
)
public class Inventory extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /* =====================================================
     *  SẢN PHẨM
     * ===================================================== */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    // snapshot – tránh join Product
    @Column(name = "product_code")
    private String productCode;

    @Column(name = "product_name")
    private String productName;

    /* =====================================================
     *  KHO
     * ===================================================== */
    @Column(nullable = false)
    private String warehouse = "DEFAULT";

    /* =====================================================
     *  SỐ LƯỢNG
     * ===================================================== */
    @Column(nullable = false)
    private Integer quantity = 0;

    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity = 0;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity = 0;

    /* =====================================================
     *  GIÁ
     * ===================================================== */
    // giá vốn hiện tại (bình quân / FIFO tuỳ nghiệp vụ)
    @Column(name = "cost_price", precision = 19, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "sale_price", precision = 19, scale = 2)
    private BigDecimal salePrice;

    /* =====================================================
     *  CẢNH BÁO TỒN
     * ===================================================== */


    /* =====================================================
     *  THỜI ĐIỂM NGHIỆP VỤ
     * ===================================================== */
    @Column(name = "last_import_date")
    private LocalDateTime lastImportDate;

    @Column(name = "last_export_date")
    private LocalDateTime lastExportDate;

    /* =====================================================
     *  TRẠNG THÁI
     * ===================================================== */
    // ACTIVE | INACTIVE | LOCKED
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryStatus status = InventoryStatus.ACTIVE;
    

    @Column(columnDefinition = "NVARCHAR(500)")
    private String note;

    /* =====================================================
     *  GETTER / SETTER
     * ===================================================== */

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity != null ? quantity : 0;
        recalcAvailable();
    }

    public Integer getReservedQuantity() {
        return reservedQuantity;
    }

    public void setReservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity != null ? reservedQuantity : 0;
        recalcAvailable();
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }



    public LocalDateTime getLastImportDate() {
        return lastImportDate;
    }

    public void setLastImportDate(LocalDateTime lastImportDate) {
        this.lastImportDate = lastImportDate;
    }

    public LocalDateTime getLastExportDate() {
        return lastExportDate;
    }

    public void setLastExportDate(LocalDateTime lastExportDate) {
        this.lastExportDate = lastExportDate;
    }

    public InventoryStatus getStatus() {
        return status;
    }

    public void setStatus(InventoryStatus status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    /* =====================================================
     *  GIÁ TRỊ TỒN KHO
     * ===================================================== */
    @Transient
    public BigDecimal getInventoryValue() {
        if (costPrice == null || quantity == null) return BigDecimal.ZERO;
        return costPrice.multiply(BigDecimal.valueOf(quantity));
    }

    /* =====================================================
     *  LOGIC TÍNH AVAILABLE
     * ===================================================== */
    private void recalcAvailable() {
        this.availableQuantity = Math.max(
            (quantity != null ? quantity : 0)
                - (reservedQuantity != null ? reservedQuantity : 0),
            0
        );
    }
}
