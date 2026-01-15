package com.example.erp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class SupplierRequestDTO {

    private Long id; // Dùng khi update

    @NotBlank(message = "Tên nhà cung cấp không được để trống")
    private String name;

    private String phone;

    @Email(message = "Email không hợp lệ")
    private String email;

    private String address;

    private String taxCode;

    private Boolean active = true;

    private Long accountId;

    // ===== Constructor =====
    public SupplierRequestDTO() {
    }

    public SupplierRequestDTO(Long id, String name, String phone, String email,
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
}
