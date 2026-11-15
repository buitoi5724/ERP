package com.example.erp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cm_code")
public class CMCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nhóm mã
    // Ví dụ: REQUEST_TYPE, REQUEST_STATUS
    @Column(nullable = false)
    private String codeGroup;

    // Mã
    // Ví dụ: PENDING, APPROVED, NGHI_PHEP
    @Column(nullable = false)
    private String code;

    // Tên hiển thị
    // Ví dụ: Chờ phê duyệt, Đã phê duyệt
    @Column(nullable = false)
    private String name;

    // Thứ tự hiển thị
    private Integer displayOrder;

    // Đang sử dụng
    @Column(nullable = false)
    private Boolean active = true;

    public CMCode() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodeGroup() {
        return codeGroup;
    }

    public void setCodeGroup(String codeGroup) {
        this.codeGroup = codeGroup;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}