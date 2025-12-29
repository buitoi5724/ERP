package com.example.erp.entity;

import java.io.Serializable;
import jakarta.persistence.*;
import com.example.erp.util.BaseEntity;

@Entity
@Table(name = "suppliers")
public class Supplier extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "account_id")
    private Long accountId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 20)
    private String phone;

    @Column(length = 100, unique = true)
    private String email;

    @Column(length = 255)
    private String address;

    @Column(length = 20, unique = true)
    private String taxCode;

    @Column(nullable = false)
    private Boolean active = true;

    // ===== Getter / Setter =====
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

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
}
