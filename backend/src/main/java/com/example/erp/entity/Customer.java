package com.example.erp.entity;

import com.example.erp.util.BaseEntity;
import com.example.erp.util.CustomerStatus;
import jakarta.persistence.*;

@Entity
@Table(
    name = "customers",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "account_id")
    }
)
public class Customer extends BaseEntity {

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "account_id",
        referencedColumnName = "id",
        insertable = false,
        updatable = false
    )
    private Account account;

    
    @Column(nullable = false, length = 150)
    private String email;

    
    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 15)
    private String phone;

    @Column(length = 255)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerStatus status = CustomerStatus.ACTIVE;

    private Long groupId;
    private Long provinceId;
    private Long districtId;
    private Long wardId;

    // ===== Getters / Setters =====
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public CustomerStatus getStatus() { return status; }
    public void setStatus(CustomerStatus status) { this.status = status; }

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public Long getProvinceId() { return provinceId; }
    public void setProvinceId(Long provinceId) { this.provinceId = provinceId; }

    public Long getDistrictId() { return districtId; }
    public void setDistrictId(Long districtId) { this.districtId = districtId; }

    public Long getWardId() { return wardId; }
    public void setWardId(Long wardId) { this.wardId = wardId; }
}
