package com.example.erp.dto;

import com.example.erp.entity.Supplier;

public class SupplierResponseDTO {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String taxCode;
    private Boolean active;
    private Long accountId;
    private String username;
    // ===== Constructors =====
    public SupplierResponseDTO() {}

    public SupplierResponseDTO(Long id, String name, String phone, String email,
                               String address, String taxCode, Boolean active,
                               Long accountId) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.taxCode = taxCode;
        this.active = active;
        this.accountId = accountId;
    }

    // 👉 Constructor tiện lợi: nhận từ Entity luôn
    public SupplierResponseDTO(Supplier supplier) {
        this.id = supplier.getId();
        this.name = supplier.getName();
        this.phone = supplier.getPhone();
        this.email = supplier.getEmail();
        this.address = supplier.getAddress();
        this.taxCode = supplier.getTaxCode();
        this.active = supplier.getActive();
        this.accountId = supplier.getAccountId();
    }

    // ===== Mapping Helper =====
    public static SupplierResponseDTO fromEntity(Supplier supplier) {
        return new SupplierResponseDTO(supplier);
    }


public SupplierResponseDTO(Supplier supplier, String username) {
    this(supplier); // Gọi lại constructor 1 tham số ở trên
    this.username = username;
}
    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getTaxCode() { return taxCode; }
    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    @Override
    public String toString() {
        return "SupplierResponseDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", taxCode='" + taxCode + '\'' +
                ", active=" + active +
                ", accountId=" + accountId +
                '}';
    }
}
