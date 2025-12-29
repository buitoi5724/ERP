package com.example.erp.dto;

public class CustomerResponseDTO {

    private Long id;
    private String code;
    private String name;
    private String email;
    private String phone;
    private String address;

    private Long accountId;
    private Long groupId;
    private Long provinceId;
    private Long districtId;
    private Long wardId;

    public CustomerResponseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public Long getProvinceId() { return provinceId; }
    public void setProvinceId(Long provinceId) { this.provinceId = provinceId; }

    public Long getDistrictId() { return districtId; }
    public void setDistrictId(Long districtId) { this.districtId = districtId; }

    public Long getWardId() { return wardId; }
    public void setWardId(Long wardId) { this.wardId = wardId; }
}
