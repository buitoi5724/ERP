package com.example.erp.dto;

public class SupplierListDTO {

    private Long id;
    private Long accountId;
    private String name;
    private String phone;
    private String email;
    private String username; // lấy từ Account
    private Boolean active;
    public SupplierListDTO() {}

    public SupplierListDTO(Long id, Long accountId, String name, String phone,
                           String email, String username, Boolean active) {
        this.id = id;
        this.accountId = accountId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.username = username;
        this.active = active;
    }

    // ===== Getter / Setter =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
