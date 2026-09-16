package com.example.erp.service;

import com.example.erp.entity.Department;

import java.util.List;

public interface DepartmentService {

    // Lấy tất cả phòng ban
    List<Department> getAll();

    // Lấy phòng ban theo ID
    Department getById(Long deptId);

    // Lấy phòng ban theo mã
    Department getByDeptCode(String deptCode);

    // Tạo phòng ban
    Department create(Department department);

    // Cập nhật phòng ban
    Department update(Long deptId, Department department);

    // Xóa phòng ban
    void delete(Long deptId);
}