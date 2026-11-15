package com.example.erp.dto;

import java.util.Set;

import com.example.erp.util.RoleType;

public class UserInfoResponse {

    private Long id;
    private String username;
    private Set<RoleType> roles;

    // Thông tin Employee
    private Long empId;
    private String empCode;
    private String empName;

    // Thông tin phòng ban hiện tại
    private Long deptId;
    private String deptCode;
    private String deptName;

    // Thông tin phê duyệt
    private boolean approver;
    private Integer approvalOrder;

    public UserInfoResponse(
            Long id,
            String username,
            Set<RoleType> roles
    ) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    public UserInfoResponse(
            Long id,
            String username,
            Set<RoleType> roles,
            Long empId,
            String empCode,
            String empName,
            Long deptId,
            String deptCode,
            String deptName,
            boolean approver,
            Integer approvalOrder
    ) {
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.empId = empId;
        this.empCode = empCode;
        this.empName = empName;
        this.deptId = deptId;
        this.deptCode = deptCode;
        this.deptName = deptName;
        this.approver = approver;
        this.approvalOrder = approvalOrder;
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

    public Long getEmpId() {
        return empId;
    }

    public String getEmpCode() {
        return empCode;
    }

    public String getEmpName() {
        return empName;
    }

    public Long getDeptId() {
        return deptId;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public String getDeptName() {
        return deptName;
    }

    public boolean isApprover() {
        return approver;
    }

    public Integer getApprovalOrder() {
        return approvalOrder;
    }
}