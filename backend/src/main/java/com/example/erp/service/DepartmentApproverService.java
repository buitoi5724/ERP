package com.example.erp.service;

import com.example.erp.entity.DepartmentApprover;

import java.util.List;

public interface DepartmentApproverService {

    // Lấy tất cả cấu hình người duyệt
    List<DepartmentApprover> getAll();

    // Lấy theo ID
    DepartmentApprover getById(Long id);

    // Lấy danh sách người duyệt của phòng ban
    List<DepartmentApprover> getByDepartment(Long deptId);

    // Lấy các phòng ban mà nhân viên được cấu hình làm người duyệt
    List<DepartmentApprover> getByEmployee(Long empId);

    // Thêm người duyệt
    DepartmentApprover create(DepartmentApprover departmentApprover);

    // Cập nhật người duyệt
    DepartmentApprover update(
            Long id,
            DepartmentApprover departmentApprover
    );

    // Xóa cấu hình người duyệt
    void delete(Long id);
}