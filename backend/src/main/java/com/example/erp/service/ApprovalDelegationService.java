package com.example.erp.service;

import com.example.erp.entity.ApprovalDelegation;

import java.time.LocalDate;
import java.util.List;

public interface ApprovalDelegationService {

    // Lấy tất cả cấu hình ủy quyền
    List<ApprovalDelegation> getAll();

    // Lấy theo ID
    ApprovalDelegation getById(Long id);

    // Lấy các ủy quyền của một nhân viên
    List<ApprovalDelegation> getByFromEmployee(Long empId);

    // Lấy các ủy quyền đang có hiệu lực
    List<ApprovalDelegation> getActiveDelegations(
            Long empId,
            LocalDate date
    );

    // Lấy các ủy quyền mà nhân viên này là người được ủy quyền
    List<ApprovalDelegation> getByToEmployee(Long empId);

    // Tạo ủy quyền
    ApprovalDelegation create(
            ApprovalDelegation approvalDelegation
    );

    // Cập nhật ủy quyền
    ApprovalDelegation update(
            Long id,
            ApprovalDelegation approvalDelegation
    );

    // Xóa ủy quyền
    void delete(Long id);
}