package com.example.erp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity // Đánh dấu đây là một Entity (tương ứng với 1 bảng trong DB)
@Table(name = "product_category") // Map tới bảng product_category trong DB
public class ProductCategory {

    @Id // Khóa chính
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    // AUTO_INCREMENT trong DB (tăng tự động)
    private Long id;

    @Column(
        nullable = false,   // Không được null
        unique = true,      // Giá trị phải duy nhất (mỗi category có 1 code riêng)
        length = 50         // Giới hạn tối đa 50 ký tự
    )
    private String code; // Mã danh mục (ví dụ: "FOOD", "ELEC")

    @Column(
        nullable = false,   // Không được null
        length = 255        // Giới hạn tối đa 255 ký tự
    )
    private String name; // Tên danh mục (ví dụ: "Đồ ăn", "Điện tử")

    @Column(columnDefinition = "TEXT") 
    // Kiểu TEXT trong DB (có thể lưu nội dung dài)
    private String description; // Mô tả chi tiết về danh mục

    // Các trường thông tin audit (theo dõi ai tạo/cập nhật và khi nào)
    private LocalDateTime createDate; // Ngày tạo
    private String createBy;          // Người tạo
    private LocalDateTime updateDate; // Ngày cập nhật
    private String updateBy;          // Người cập nhật

    // Constructor mặc định (bắt buộc cho JPA/Hibernate)
    public ProductCategory() {}

    // ------------------------
    // Getters & Setters
    // ------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreateDate() { return createDate; }
    public void setCreateDate(LocalDateTime createDate) { this.createDate = createDate; }

    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }

    public LocalDateTime getUpdateDate() { return updateDate; }
    public void setUpdateDate(LocalDateTime updateDate) { this.updateDate = updateDate; }

    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }
}
