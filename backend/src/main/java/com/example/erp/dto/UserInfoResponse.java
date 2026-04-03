package com.example.erp.dto;

import java.util.Set;
import com.example.erp.util.RoleType;

public class UserInfoResponse {

    private Long id;
    private String username;
    private Set<RoleType> roles;

    public UserInfoResponse(Long id, String username, Set<RoleType> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Set<RoleType> getRoles() {
        return roles;
    }
}
